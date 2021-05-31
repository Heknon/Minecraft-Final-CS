package render.texture

import org.lwjgl.opengl.GL30

class TextureAtlas(textureFileName: String, private val gridSize: Int) {
    val texture: Texture = Texture(textureFileName)

    /**
     * Based on top left coordinate of texture in texture atlas
     */
    fun getTexture(x: Int, y: Int): AtlasTexture {
        if (x > gridSize || y > gridSize) {
            throw IllegalArgumentException("x ($x) and y ($y) coordinates must be less then grid size $gridSize")
        }

        val xMin = x.toFloat() / gridSize
        val xMax = (x + 1f) / gridSize
        val yMin = y.toFloat() / gridSize
        val yMax = (y + 1f) / gridSize

        return AtlasTexture(
            Pair(xMin, yMin),
            Pair(xMax, yMin),
            Pair(xMin, yMax),
            Pair(xMax, yMax)
        )
    }

    fun activate() {
        GL30.glActiveTexture(GL30.GL_TEXTURE0)
        texture.bind()
        texture.bind()
    }

    fun empty(): AtlasTexture {
        return AtlasTexture(
            Pair(0f, 0f),
            Pair(0f, 0f),
            Pair(0f, 0f),
            Pair(0f, 0f)
        )
    }

    data class AtlasTexture(
        val topLeft: Pair<Float, Float>,
        val topRight: Pair<Float, Float>,
        val bottomLeft: Pair<Float, Float>,
        val bottomRight: Pair<Float, Float>
    ) {
        fun getDefaultUVArray(): FloatArray {
            return floatArrayOf(
                topLeft.first, topLeft.second,
                bottomLeft.first, bottomLeft.second,
                bottomRight.first, bottomRight.second,
                topRight.first, topRight.second,
            )
        }
    }
}