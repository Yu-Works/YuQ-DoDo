package com.icecreamqaq.yuq.dodo.message

import com.icecreamqaq.yuq.dodo.OpDoDo
import com.icecreamqaq.yuq.dodo.opDoDo
import com.icecreamqaq.yuq.message.Message
import com.icecreamqaq.yuq.message.MessageSource

class SendMessage {
    private val currentBuilder: StringBuilder = StringBuilder()
    private val messageList: MutableList<Pair<Int, OpDoDo.IMessageBody>> = arrayListOf()

    val result: MutableList<Pair<Int, OpDoDo.IMessageBody>>
        get() {
            save()
            return messageList
        }

    fun save() {
        if (currentBuilder.isEmpty()) return
        messageList.add(1 to OpDoDo.TextMessageBody(currentBuilder.toString()))
        currentBuilder.clear()
    }

    fun add(type: Int, body: OpDoDo.IMessageBody) {
        save()
        messageList.add(type to body)
    }

    fun append(body: String) {
        currentBuilder.append(body)
    }
}

fun make(item: DoDOItemBase, sm: SendMessage) {
    item.run { sm.run { this.make() } }
}

fun Message.toLocal() =
    SendMessage().apply { body.forEach { make(it as DoDOItemBase, this) } }.result