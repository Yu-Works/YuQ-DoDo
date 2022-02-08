package com.icecreamqaq.yuq.dodo

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.util.Web
import com.icecreamqaq.yuq.YuQ
import com.icecreamqaq.yuq.YuQVersion
import com.icecreamqaq.yuq.entity.Friend
import com.icecreamqaq.yuq.entity.Group
import com.icecreamqaq.yuq.entity.User
import com.icecreamqaq.yuq.entity.UserListImpl
import com.icecreamqaq.yuq.message.MessageItemFactory
import javax.inject.Inject

class DoDo : ApplicationService, YuQVersion, YuQ {

    @Config("YuQ.DoDo.clientId")
    lateinit var clientId: String

    @Config("YuQ.DoDo.token")
    lateinit var token: String

    @Config("YuQ.DoDo.publicKey")
    lateinit var publicKey: String

    @Config("YuQ.DoDo.clientSecret")
    lateinit var clientSecret: String

    lateinit var op: OpDoDo

    override fun init() {
        op = OpDoDo(clientId, token, publicKey, clientSecret)

        op.run {
            println(botInfo())
//            println(guildList())
            println(guildInfo(199899))
            println(channelList(199899))
            val id = sendMessageToChannel(177531, 1, "测试消息1").messageId
            println(
                sendMessageToChannel(
                    177531,
                    1,
                    "**测试***消*[息](https://www.baidu.com/)2__456__~~测试~~||防剧透||\n>123456\n1`23`4\n1```23456```6"
                )
            )
            println(recallMessage(id))
            ws()
        }
    }

    override fun start() {
//        op.ws()
    }

    override fun stop() {
        op.close()
    }


    override fun platform() = "DoDo"
    override fun runtimeName() = "YuQ-DoDo"
    override fun runtimeVersion() = "0.1.0-DEV"

    override val botId: Long
        get() = clientId.toLong()
    override val botInfo: User
        get() = TODO("Not yet implemented")
    override val cookieEx: YuQ.QQCookie
        get() = TODO("Not yet implemented")
    override val friends = UserListImpl<Friend>()
    override val groups = UserListImpl<Group>()
    override val messageItemFactory: MessageItemFactory
        get() = TODO("Not yet implemented")

    @Inject
    override lateinit var web: Web

    override fun id2platformId(id: Long) = id.toString()
    override fun platformId2id(platformId: String) = platformId.toLong()

    override fun refreshFriends() = friends
    override fun refreshGroups() = groups


}