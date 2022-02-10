package com.icecreamqaq.yuq.dodo.message

import com.icecreamqaq.yuq.dodo.dodo
import com.icecreamqaq.yuq.entity.Contact
import com.icecreamqaq.yuq.message.Message
import com.icecreamqaq.yuq.message.MessageSource

internal fun <T, R : MessageSource> Message.send(contact: Contact, obj: T, send: (T) -> R): R =
    dodo.internalBot.sendMessage(this, contact, obj, send)

internal fun String.toMessageByDoDoCode(){

}
