package mesh

import Texture
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(positions: FloatArray, textCoords: FloatArray, indices: IntArray, private val texture: Texture) {
    val vaoId: Int

    val vertexCount: Int = indices.size

    private val posVboId: Int

    private val indexVboId: Int

    private val textureVboId: Int



    init {
        var positionsBuffer: FloatBuffer? = null
        var textureBuffer: FloatBuffer? = null
        var indicesBuffer: IntBuffer? = null

        try {
            vaoId = glGenVertexArrays()
            glBindVertexArray(vaoId)

            // Position VBO
            posVboId = GL15.glGenBuffers()
            positionsBuffer = MemoryUtil.memAllocFloat(positions.size)
            positionsBuffer.put(positions).flip()
            glBindBuffer(GL15.GL_ARRAY_BUFFER, posVboId)
            glBufferData(GL15.GL_ARRAY_BUFFER, positionsBuffer!!, GL15.GL_STATIC_DRAW)
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(0, 3, GL15.GL_FLOAT, false, 0, 0)

            // Texture VBO
            textureVboId = GL15.glGenBuffers()
            textureBuffer = MemoryUtil.memAllocFloat(textCoords.size)
            textureBuffer.put(textCoords).flip()
            glBindBuffer(GL15.GL_ARRAY_BUFFER, textureVboId)
            glBufferData(GL15.GL_ARRAY_BUFFER, textureBuffer!!, GL15.GL_STATIC_DRAW)
            glEnableVertexAttribArray(1)
            glVertexAttribPointer(1, 2, GL15.GL_FLOAT, false, 0, 0)

            // Index VBO
            indexVboId = GL15.glGenBuffers()
            indicesBuffer = MemoryUtil.memAllocInt(indices.size)
            indicesBuffer.put(indices).flip()
            glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVboId)
            GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer!!, GL_STATIC_DRAW)

            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)

        } finally {
            if (positionsBuffer != null) {
                MemoryUtil.memFree(positionsBuffer)
            }

            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer)
            }

            if (textureBuffer != null) {
                MemoryUtil.memFree(textureBuffer)
            }
        }
    }

    fun cleanup() {
        glDisableVertexAttribArray(0)

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL15.glDeleteBuffers(posVboId)
        GL15.glDeleteBuffers(indexVboId)
        GL15.glDeleteBuffers(textureVboId)

        texture.cleanup()

        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }

    fun render() {
        glActiveTexture(GL_TEXTURE0)

        glBindTexture(GL_TEXTURE_2D, texture.id)

        glBindVertexArray(vaoId)

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)

        glBindVertexArray(0)
    }
}