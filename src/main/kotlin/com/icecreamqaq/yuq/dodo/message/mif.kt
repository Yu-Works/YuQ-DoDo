package com.icecreamqaq.yuq.dodo.message

import com.icecreamqaq.yuq.entity.Member
import com.icecreamqaq.yuq.message.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream

class DoDoMIF:MessageItemFactory {

    override fun text(text: String) = TextImpl(text)

    override fun at(member: Member): At {
        TODO("Not yet implemented")
    }

    override fun at(qq: Long): At {
        TODO("Not yet implemented")
    }

    override fun face(id: Int): Face {
        TODO("Not yet implemented")
    }

    override fun imageByBufferedImage(bufferedImage: BufferedImage): Image {
        TODO("Not yet implemented")
    }

    override fun imageByFile(file: File): Image {
        TODO("Not yet implemented")
    }

    override fun imageById(id: String): Image {
        TODO("Not yet implemented")
    }

    override fun imageByInputStream(inputStream: InputStream): Image {
        TODO("Not yet implemented")
    }

    override fun imageByUrl(url: String): Image {
        TODO("Not yet implemented")
    }

    override fun imageToFlash(image: Image): FlashImage {
        TODO("Not yet implemented")
    }

    override fun jsonEx(value: String): JsonEx {
        TODO("Not yet implemented")
    }

    override fun messagePackage(flag: Int, body: MutableList<IMessageItemChain>): MessagePackage {
        TODO("Not yet implemented")
    }


    override fun voiceByInputStream(inputStream: InputStream): Voice {
        TODO("Not yet implemented")
    }

    override fun xmlEx(serviceId: Int, value: String): XmlEx {
        TODO("Not yet implemented")
    }
}