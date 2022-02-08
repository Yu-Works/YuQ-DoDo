package com.icecreamqaq.yuq.dodo.entity

import com.icecreamqaq.yuq.YuQ
import com.icecreamqaq.yuq.dodo.OpDoDo
import com.icecreamqaq.yuq.dodo.dodo
import com.icecreamqaq.yuq.dodo.message.DoDoMessageSource
import com.icecreamqaq.yuq.dodo.message.DoDoMultiMessageSource
import com.icecreamqaq.yuq.dodo.message.send
import com.icecreamqaq.yuq.dodo.message.toLocal
import com.icecreamqaq.yuq.dodo.opDoDo
import com.icecreamqaq.yuq.entity.Channel
import com.icecreamqaq.yuq.entity.Guild
import com.icecreamqaq.yuq.entity.GuildMember
import com.icecreamqaq.yuq.entity.UserListImpl
import com.icecreamqaq.yuq.message.Image
import com.icecreamqaq.yuq.message.Message
import com.icecreamqaq.yuq.message.MessageSource
import java.io.File

class GuildImpl(
    override val id: Long,
    override var name: String,
    override val avatar: String
) : Guild {

    private var defaultChannelField: Channel? = null
    override var channels = UserListImpl<ChannelImpl>()
    override val member = UserListImpl<MemberImpl>()

    init {
        readChannels()
    }


    private fun readChannels() {
        val cs = opDoDo.channelList(id)
        val ncs = UserListImpl<ChannelImpl>()
        cs.forEach {
            (channels[it.channelId]?.apply {
                if (name != it.channelName) name = it.channelName
            } ?: ChannelImpl(this, it.channelId, it.channelName)).apply {
                if (it.defaultFlag == 1) defaultChannelField = this
                ncs.add(this)
            }
        }
    }

    override val platformId: String = id.toString()
    override fun canSendMessage() = false
    override fun isFriend() = false

    fun getOrNewChannel(channelId: Long): ChannelImpl =
        channels.getOrPut(channelId){
            opDoDo.channelInfo(channelId).let {
                ChannelImpl(
                    this,
                    it.channelId,
                    it.channelName
                )
            }
        }

    fun getOrNewMember(member: Long): MemberImpl =
        this.member.getOrPut(member){
            opDoDo.memberInfo(id,member).let {
                MemberImpl(
                    this,
                    it.dodoId,
                    it.nickName,
                    it.avatarUrl
                )
            }
        }

    override val defaultChannel: Channel
        get() = defaultChannelField!!

}

class ChannelImpl(
    override val guild: Guild,
    override val id: Long,
    override var name: String
) : Channel {

    override val yuq: YuQ
        get() = com.icecreamqaq.yuq.yuq

    override val guid = "gc${guild.id}_$id"
    override val platformId: String = id.toString()
    override fun toLogString() = "${guild.name}:$name(${guild.id}:$id)"
    override fun isFriend() = false


    override fun sendMessage(message: Message): MessageSource {
        return message.send(this, null) {
            val reqs = message.toLocal().map {
                OpDoDo.SendMessageReq(
                    channelId = id,
                    messageType = it.first,
                    messageBody = it.second
                )
            }
            message.reply?.let { reqs.last().referencedMessageId = (it as DoDoMessageSource).realId }
            DoDoMultiMessageSource(
                reqs.map { opDoDo.sendMessageToChannel(it).messageId }.toTypedArray(),
                dodo.botId,
                System.currentTimeMillis()
            )
        }
    }

    override fun sendFile(file: File) {
        TODO("Not yet implemented")
    }

    override fun uploadImage(imageFile: File): Image {
        TODO("Not yet implemented")
    }

}

class MemberImpl(
    override val guild: Guild,
    override val id: Long,
    override val name: String,
    override val avatar: String
) : GuildMember {
    override val yuq: YuQ
        get() = com.icecreamqaq.yuq.yuq

    override val guid = "gm${guild.id}_$id"
    override val platformId = id.toString()

    override fun toLogString() = "$name($id)"
    override fun isFriend() = false

    override fun sendFile(file: File) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(message: Message): MessageSource {
        TODO("Not yet implemented")
    }

    override fun uploadImage(imageFile: File): Image {
        TODO("Not yet implemented")
    }

}
