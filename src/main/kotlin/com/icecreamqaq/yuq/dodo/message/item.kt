package com.icecreamqaq.yuq.dodo.message

import com.icecreamqaq.yuq.annotation.PathVar
import com.icecreamqaq.yuq.entity.Contact
import com.icecreamqaq.yuq.message.MessageItem
import com.icecreamqaq.yuq.message.MessageItemBase
import com.icecreamqaq.yuq.message.Text

abstract class DoDOItemBase : MessageItemBase() {

    override fun toLocal(contact: Contact): Any {
        TODO("Not yet implemented")
    }

    abstract fun SendMessage.make()


}

class TextImpl(override val text: String) : DoDOItemBase(), Text {
    override fun SendMessage.make() = append(text)
}


