package com.icecreamqaq.yuq.dodo.message

import com.icecreamqaq.yuq.annotation.PathVar
import com.icecreamqaq.yuq.dodo.OpDoDo
import com.icecreamqaq.yuq.entity.Contact
import com.icecreamqaq.yuq.message.Image
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

class ImageRecv(override val url: String) : DoDOItemBase(), Image {
    override val id: String
        get() = url

    override fun SendMessage.make() {
        add(2, OpDoDo.ImageMessageBody(url))
    }
}

class VideoRecv(
    val url: String,
    val cover: String?,
) : DoDOItemBase(), MessageItem {
    val id: String
        get() = url

    override fun SendMessage.make() {
        add(
            3, OpDoDo.VideoMessageBody(
                url = url,
                coverUrl = cover
            )
        )
    }

    override fun convertByPathVar(type: PathVar.Type): Any? = when (type) {
        PathVar.Type.String -> "视频"
        PathVar.Type.Source -> this
        else -> null
    }

    override fun equal(other: MessageItem): Boolean {
        if (other !is Image) return false
        return id == other.id
    }

    override fun toPath() = "视频"

}


