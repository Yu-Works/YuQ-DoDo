package com.icecreamqaq.yuq.dodo

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.event.EventBus
import com.IceCreamQAQ.Yu.util.Web
import com.icecreamqaq.yuq.*
import com.icecreamqaq.yuq.dodo.entity.GuildImpl
import com.icecreamqaq.yuq.entity.*
import com.icecreamqaq.yuq.message.MessageItemFactory
import javax.inject.Inject

class DoDo : ApplicationService, YuQVersion, YuQ, User {

    @Config("YuQ.DoDo.clientId")
    lateinit var clientId: String

    @Config("YuQ.DoDo.token")
    lateinit var token: String

    @Config("YuQ.DoDo.publicKey")
    lateinit var publicKey: String

    @Config("YuQ.DoDo.clientSecret")
    lateinit var clientSecret: String

    lateinit var op: OpDoDo

    @Inject
    override lateinit var messageItemFactory: MessageItemFactory

    @Inject
    override lateinit var web: Web

    @Inject
    lateinit var internalBot: YuQInternalBotImpl

    @Inject
    lateinit var context: YuContext

    @Inject
    lateinit var eventBus:EventBus


    override fun init() {
        op = OpDoDo(clientId, token, publicKey, clientSecret)

        dodo = this
        opDoDo = op
        mif = messageItemFactory
        com.icecreamqaq.yuq.eventBus = eventBus

        op.run {
            botInfo().let {
                id = it.dodoId
                name = it.nickName
                avatar = it.avatarUrl
            }
            println(guildInfo(199899))
        }
//        refreshGuilds()
    }

    private fun getOrNewGuild(id: Long): GuildImpl =
        guilds.getOrPut(id) {
            opDoDo.guildInfo(id).let {
                GuildImpl(
                    id,
                    it.isLandName,
                    it.coverUrl
                )
            }
        }


    override fun start() {
        context.injectBean(internalBot)
        op.ws()
    }

    override fun stop() {
        op.close()
    }


    override fun platform() = "DoDo"
    override fun runtimeName() = "YuQ-DoDo"
    override fun runtimeVersion() = "0.1.0-DEV"


    override var guilds = UserListImpl<GuildImpl>()

    override fun refreshGuilds(): GuildList {
        val gs = op.guildList()
        val ngs = UserListImpl<GuildImpl>()
        gs.forEach {
            ngs.add(
                guilds[it.islandId]?.apply {
                    if (name != it.isLandName) name = it.isLandName
                } ?: GuildImpl(it.islandId, it.isLandName, it.coverUrl)
            )
        }
        guilds = ngs
        return guilds
    }

    override val botId: Long
        get() = id
    override val botInfo: User
        get() = this

    override var id: Long = 0
    override val platformId: String
        get() = id.toString()

    override var name = ""
    override var avatar: String = ""

    override fun canSendMessage() = false
    override fun isFriend() = false

    override fun id2platformId(id: Long) = id.toString()
    override fun platformId2id(platformId: String) = platformId.toLong()

    override val friends = UserListImpl<Friend>()
    override val groups = UserListImpl<Group>()
    override fun refreshFriends() = friends
    override fun refreshGroups() = groups

    override val cookieEx: YuQ.QQCookie
        get() = TODO("Not yet implemented")

}