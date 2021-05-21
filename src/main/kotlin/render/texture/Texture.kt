package render.texture

import de.matthiasmann.twl.utils.PNGDecoder
import org.lwjgl.opengl.GL11.*
import java.nio.ByteBuffer

class Texture(fileName: String) {
    val id: Int

    init {
        val decoder = PNGDecoder(this::class.java.getResourceAsStream(fileName))
        val buf = ByteBuffer.allocateDirect(4 * decoder.width * decoder.height)
        decoder.decode(buf, decoder.width * 4, PNGDecoder.Format.RGBA)
        buf!!.flip()


        id = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, id)
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.width, decoder.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        //glGenerateMipmap(GL_TEXTURE_2D)


    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun cleanup() {
        glDeleteTextures(id)
    }
}