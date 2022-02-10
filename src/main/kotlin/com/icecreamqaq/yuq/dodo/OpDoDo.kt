package com.icecreamqaq.yuq.dodo

import com.IceCreamQAQ.Yu.toJSONObject
import com.IceCreamQAQ.Yu.toJSONString
import com.IceCreamQAQ.Yu.toObject
import com.alibaba.fastjson.JSONObject
import com.icecreamqaq.yuq.dodo.message.ImageRecv
import com.icecreamqaq.yuq.dodo.message.TextImpl
import com.icecreamqaq.yuq.dodo.message.VideoRecv
import com.icecreamqaq.yuq.message.Message
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher

class OpDoDo(
    private val clientId: String,
    private val token: String,
    private val publicKey: String,
    private val clientSecret: String,
) {

    private inner class RsaUtil {

        private val publicKey: RSAPublicKey

        init {
            val keyFactory = KeyFactory.getInstance("RSA")

            publicKey = keyFactory.generatePublic(
                X509EncodedKeySpec(
                    Base64.getDecoder().decode(this@OpDoDo.publicKey)
                )
            ) as RSAPublicKey
        }

        fun encryptByPublic(content: ByteArray): String {
            try {
                val cipher = Cipher.getInstance("RSA")
                cipher.init(Cipher.ENCRYPT_MODE, publicKey)
                return Base64.getEncoder().encodeToString(cipher.doFinal(content))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

    }

    private val rsa = RsaUtil()
    private fun String.rsaEncode() = rsa.encryptByPublic(this.toByteArray())


    private val web = OkHttpClient
        .Builder()
        .pingInterval(10,TimeUnit.SECONDS)
        .build()

    private fun postV1(path: String, data: Any? = null): JSONObject {
        val sd = data?.toJSONString()
        val time = System.currentTimeMillis().toString()
        val resp = web.newCall(
            Request
                .Builder()
                .url("https://botopen.imdodo.com/api/v1/$path")
                .post((sd ?: "{}").toRequestBody("application/json".toMediaType()))
                .header("clientId", clientId)
                .header("Authorization", token)
                .header("timestamp", time)
                .header("sign", "$time$clientSecret".rsaEncode())
                .build()
        ).execute().body!!.string().toJSONObject()
        val code = resp.getIntValue("status")
        val message = resp.getString("message") ?: "无返回 Message！"
        if (code != 0) error("请求错误！Status: $code, Message: $message！")
        return resp
    }

    private inline fun <reified T> readV1(path: String, data: Any? = null): T =
        postV1(path, data).getObject("data", T::class.java)

    private inline fun <reified T> readArrayV1(path: String, data: Any? = null): List<T> =
        postV1(path, data).getJSONArray("data").toJavaList(T::class.java)

    data class BotInfo(
        val clientId: Long,
        val dodoId: Long,
        val nickName: String,
        val avatarUrl: String
    )

    fun botInfo(): BotInfo = readV1("bot/info")

    data class Island(
        val islandId: Long,
        val isLandName: String,
        val coverUrl: String,
        val defaultChannelId: String,
        val systemChannelId: String
    )

    fun guildList(): List<Island> = readArrayV1("island/list")

    data class OnlyIslandIdReq(val islandId: Long)
    data class OnlyChannelIdReq(val channelId: Long)

    fun onlyGuildId(id: Long) = OnlyIslandIdReq(id)

    fun guildInfo(id: Long): Island = readV1("island/info", onlyGuildId(id))

    data class Channel(
        val channelId: Long,
        val channelName: String,
        val defaultFlag: Int
    )

    fun channelList(id: Long): List<Channel> = readArrayV1("channel/list", onlyGuildId(id))
    fun channelInfo(id: Long): Channel = readV1("channel/info", OnlyChannelIdReq(id))

    data class MemberInfoReq(
        val islandId: Long,
        val dodoId: Long
    )

    data class MemberInfo(
        val islandId: Long,
        val dodoId: Long,
        val nickName: String,
        val avatarUrl: String,
        val sex: Int
    )

    fun memberInfo(guild: Long, member: Long) = readV1<MemberInfo>("member/info", MemberInfoReq(guild, member))

    interface IMessageBody {
        fun toMessage(): Message
    }

    data class TextMessageBody(val content: String) : IMessageBody {
        override fun toMessage() = TextImpl(content).toMessage()
    }

    data class ImageMessageBody(
        val url: String,
        val width: Int? = null,
        val height: Int? = null,
        val isOriginal: Int? = null
    ) : IMessageBody {
        override fun toMessage() = ImageRecv(url).toMessage()
    }

    data class VideoMessageBody(
        val url: String,
        val coverUrl: String? = null,
        val duration: Long? = null,
        val size: Long? = null
    ) : IMessageBody {
        override fun toMessage() = VideoRecv(url,coverUrl).toMessage()
    }

    data class SendMessageReq(
        val channelId: Long,
        val messageType: Int,
        val messageBody: IMessageBody,
        var referencedMessageId: Long? = null
    )

    data class OnlyMessageId(val messageId: Long)

    fun sendMessageToChannel(sendMessageReq: SendMessageReq): OnlyMessageId =
        readV1(
            "channel/message/send",
            sendMessageReq
        )

    fun sendMessageToChannel(channelId: Long, type: Int, message: String, referer: Long? = null): OnlyMessageId =
        sendMessageToChannel(
            SendMessageReq(
                channelId,
                type,
                TextMessageBody(message),
                referer
            )
        )

    fun recallMessage(messageId: Long) = postV1("channel/message/withdraw", OnlyMessageId(messageId))

    data class WsResp(val endPoint: String)

    data class Recv(
        val type: Int,
        val data: JSONObject
    )

    data class PushEventRecv(
        val eventId: String,
        val eventType: Int,
        val eventBody: JSONObject,
        val timestamp: Long
    )

    data class MessageEventRecv(
        val islandId: Long,
        val channelId: Long,
        val dodoId: Long,
        val messageId: Long,
        val messageType: Int,
        val messageBody: JSONObject,
        val referencedMessageId: Long?
    )

    private lateinit var messageListener: (MessageEventRecv, IMessageBody, PushEventRecv) -> Unit
    fun onMessage(body: (MessageEventRecv, IMessageBody, PushEventRecv) -> Unit) {
        messageListener = body
    }

    inner class WsListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("connect success.")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            val recv = bytes.utf8().toObject<Recv>()
            if (recv.type == 0) {
                val event = recv.data.toJavaObject(PushEventRecv::class.java)
                if (event.eventType == 2001) {
                    val body = event.eventBody.toJavaObject(MessageEventRecv::class.java)
                    val messageBody = body.messageBody.toJavaObject(
                        when (body.messageType) {
                            1 -> TextMessageBody::class.java
                            2 -> ImageMessageBody::class.java
                            3 -> VideoMessageBody::class.java
                            else -> error("遇到无法解析的 MessageType: ${body.messageType}！")
                        }
                    )
                    messageListener(body, messageBody,event)
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            println("connect end.")
        }
    }

    private val wsListener = WsListener()
    private lateinit var wsSession: WebSocket
    var close = false
    fun ws() {
        val wsPath = readV1<WsResp>("websocket/connection").endPoint
        wsSession = web.newWebSocket(Request.Builder().url(wsPath).build(), wsListener)
    }

    fun close() {
        close = true
        wsSession.close(0, "")
    }


}