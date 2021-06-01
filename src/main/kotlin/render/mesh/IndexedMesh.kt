package render.mesh

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import utility.putList
import java.nio.FloatBuffer
import java.nio.IntBuffer

interface IndexedMesh : Mesh {
    val indices: List<Int>

    fun initializeIndexVBO(buffer: IntBuffer) {
        val vboId = GL15.glGenBuffers()
        bufferIds.add(vboId)
        buffer.putList(indices).flip()
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId)
        GL15.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW)

        MemoryUtil.memFree(buffer)
    }

    override fun initVBOs() {
        super.initVBOs()
        val indicesBuffer = MemoryUtil.memAllocInt(indices.size)
        initializeIndexVBO(indicesBuffer)
    }
}