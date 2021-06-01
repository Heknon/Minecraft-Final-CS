package render.mesh

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import render.texture.Texture
import utility.putList
import java.nio.FloatBuffer
import java.nio.IntBuffer

interface Mesh {
    val bufferIds: MutableList<Int>
    val positions: List<Float>
    var vaoId: Int
    val vertexCount: Int

    fun initializePositionVBO(buffer: FloatBuffer) {
        val vboId = GL15.glGenBuffers()
        bufferIds.add(vboId)
        buffer.putList(positions).flip()
        glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL15.GL_FLOAT, false, 0, 0)

        MemoryUtil.memFree(buffer)
    }


    fun initVBOs() {
        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        val positionsBuffer = MemoryUtil.memAllocFloat(positions.size)
        initializePositionVBO(positionsBuffer)
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

    fun render() {
        glBindVertexArray(vaoId)

        if (this is IndexedMesh) {
            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)
        } else {
            glDrawArrays(GL_TRIANGLES, 0, vertexCount)
        }

        glBindVertexArray(0)
    }
}