package render.texture

import render.mesh.Mesh

class TextureProvider(textureAtlasPath: String, atlasSize: Int) {
    private val textureAtlas = TextureAtlas(textureAtlasPath, atlasSize)

    fun activateAtlas() {
        textureAtlas.activate()
    }

    fun createAtlasMesh(positions: List<Float>, uvs: List<Float>, indices: List<Int>?): Mesh {
        return Mesh(positions, uvs, indices, textureAtlas.texture)
    }

    fun getTexture(x: Int, y: Int): TextureAtlas.AtlasTexture {
        return textureAtlas.getTexture(x, y)
    }

    fun emptyTexture(): TextureAtlas.AtlasTexture {
        return textureAtlas.empty()
    }

    fun cleanup() {
        textureAtlas.texture.cleanup()
    }
}