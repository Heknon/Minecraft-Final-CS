package render.mesh

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import render.texture.Texture
import utility.putList
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(val positions: List<Float>, val uvs: List<Float>, val indices: List<Int>?, private val texture: Texture) {
    private var vaoId: Int = 0

    private val vertexCount: Int = indices?.size ?: positions.size

    private val hasIndices = indices != null

    private val bufferIds = mutableListOf<Int>()


    init {
        var positionsBuffer: FloatBuffer? = null
        var textureBuffer: FloatBuffer? = null
        var indicesBuffer: IntBuffer? = null

        try {
            vaoId = glGenVertexArrays()
            glBindVertexArray(vaoId)

            // Position VBO
            val posVboId = GL15.glGenBuffers()
            bufferIds.add(posVboId)
            positionsBuffer = MemoryUtil.memAllocFloat(positions.size)
            positionsBuffer.putList(positions).flip()
            glBindBuffer(GL15.GL_ARRAY_BUFFER, posVboId)
            glBufferData(GL15.GL_ARRAY_BUFFER, positionsBuffer!!, GL15.GL_STATIC_DRAW)
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(0, 3, GL15.GL_FLOAT, false, 0, 0)

            // Texture VBO
            val textureVboId = GL15.glGenBuffers()
            bufferIds.add(textureVboId)
            textureBuffer = MemoryUtil.memAllocFloat(uvs.size)
            textureBuffer.putList(uvs).flip()
            glBindBuffer(GL15.GL_ARRAY_BUFFER, textureVboId)
            glBufferData(GL15.GL_ARRAY_BUFFER, textureBuffer!!, GL15.GL_STATIC_DRAW)
            glEnableVertexAttribArray(1)
            glVertexAttribPointer(1, 2, GL15.GL_FLOAT, false, 0, 0)

            // Index VBO
            if (hasIndices) {
                val indexVboId = GL15.glGenBuffers()
                bufferIds.add(indexVboId)
                indicesBuffer = MemoryUtil.memAllocInt(indices!!.size)
                indicesBuffer.putList(indices).flip()
                glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVboId)
                GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer!!, GL_STATIC_DRAW)
            }

            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)

        } finally {
            if (positionsBuffer != null) {
                MemoryUtil.memFree(positionsBuffer)
            }

            if (hasIndices && indicesBuffer != null) {
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

        for (bufferId in bufferIds) {
            GL15.glDeleteBuffers(bufferId)
        }

        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }

    fun render(activateTexture: Boolean = false) {
        if (activateTexture) {
            glActiveTexture(GL_TEXTURE0)

            glBindTexture(GL_TEXTURE_2D, texture.id)
        }

        glBindVertexArray(vaoId)

        if (hasIndices) {
            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)
        } else {
            glDrawArrays(GL_TRIANGLES, 0, vertexCount)
        }

        glBindVertexArray(0)
    }
}