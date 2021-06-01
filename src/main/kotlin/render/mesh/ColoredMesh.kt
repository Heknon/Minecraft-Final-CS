package render.mesh

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import render.texture.Texture
import utility.putList
import java.nio.FloatBuffer

open class ColoredMesh(
    final override val positions: MutableList<Float>,
    val colors: MutableList<Float>,
    autoInit: Boolean = false
) : Mesh {
    final override var vaoId: Int = GL30.glGenVertexArrays()
    override val bufferIds: MutableList<Int> = mutableListOf()
    override val vertexCount: Int = positions.size

    fun initializeColorVBO(buffer: FloatBuffer) {
        val vboId = GL15.glGenBuffers()
        bufferIds.add(vboId)
        buffer.putList(colors).flip()
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL30.glEnableVertexAttribArray(2)
        GL30.glVertexAttribPointer(2, 4, GL15.GL_FLOAT, false, 0, 0)

        MemoryUtil.memFree(buffer)
    }

    override fun initVBOs() {
        super.initVBOs()
        val colorsBuffer = MemoryUtil.memAllocFloat(colors.size)
        initializeColorVBO(colorsBuffer)
    }

    init {
        GL30.glBindVertexArray(vaoId)
        if (autoInit) initVBOs()
    }
}

class ColoredMeshIndexed(
    positions: MutableList<Float>,
    colors: MutableList<Float>,
    override val indices: MutableList<Int>
) : ColoredMesh(positions, colors, false), IndexedMesh {
    override val vertexCount: Int = indices.size

    override fun initVBOs() {
        super<IndexedMesh>.initVBOs()

        val uvsBuffer = MemoryUtil.memAllocFloat(colors.size)
        initializeColorVBO(uvsBuffer)
    }



}