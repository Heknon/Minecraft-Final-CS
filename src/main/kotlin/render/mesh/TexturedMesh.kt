package render.mesh

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import render.texture.Texture
import utility.putList
import java.nio.FloatBuffer
import java.nio.IntBuffer

open class TexturedMesh(
    final override val positions: List<Float>,
    val uvs: List<Float>,
    val texture: Texture?,
    var activateTexturesOnRender: Boolean = false,
    autoInit: Boolean = true
) : Mesh {
    override val bufferIds: MutableList<Int> = mutableListOf()
    final override var vaoId: Int = GL30.glGenVertexArrays()
    override val vertexCount: Int = positions.size

    init {
        GL30.glBindVertexArray(vaoId)
        if (autoInit) initVBOs()
    }

    fun initializeTextureVBO(buffer: FloatBuffer) {
        val vboId = GL15.glGenBuffers()
        bufferIds.add(vboId)
        buffer.putList(uvs).flip()
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL30.glEnableVertexAttribArray(1)
        GL30.glVertexAttribPointer(1, 2, GL15.GL_FLOAT, false, 0, 0)

        MemoryUtil.memFree(buffer)
    }

    override fun initVBOs() {
        super.initVBOs()
        val uvsBuffer = MemoryUtil.memAllocFloat(uvs.size)
        initializeTextureVBO(uvsBuffer)
    }

    override fun render() {
        if (activateTexturesOnRender) {
            GL30.glActiveTexture(GL30.GL_TEXTURE0)

            if (texture != null) GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture.id)
        }

        super.render()
    }
}

class TexturedMeshIndexed(
    positions: List<Float>,
    uvs: List<Float>,
    override val indices: List<Int>,
    texture: Texture?,
    activateTexturesOnRender: Boolean = false
) : TexturedMesh(positions, uvs, texture, activateTexturesOnRender, false), IndexedMesh {
    override val vertexCount: Int = indices.size

    init {
        GL30.glBindVertexArray(vaoId)
        initVBOs()
    }

    override fun initVBOs() {
        super<IndexedMesh>.initVBOs()

        val uvsBuffer = MemoryUtil.memAllocFloat(uvs.size)
        initializeTextureVBO(uvsBuffer)
    }



}