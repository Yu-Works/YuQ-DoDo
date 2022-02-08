package com.icecreamqaq.yuq.dodo.message

import com.icecreamqaq.yuq.dodo.opDoDo
import com.icecreamqaq.yuq.message.MessageSource

class DoDoMessageSource(
    val realId: Long,
    override val sender: Long,
    override val sendTime: Long,
) : MessageSource {

    override val id = realId.hashCode()
    override val liteMsg: String
        get() = ""

    override fun recall(): Int {
        opDoDo.recallMessage(realId)
        return 0
    }
}

class DoDoMultiMessageSource(
    val readIds: Array<Long>,
    override val sender: Long,
    override val sendTime: Long,
) : MessageSource {
    override val id = readIds.hashCode()
    override val liteMsg: String
        get() = ""

    override fun recall(): Int {
        readIds.forEach { opDoDo.recallMessage(it) }
        return 0
    }
}