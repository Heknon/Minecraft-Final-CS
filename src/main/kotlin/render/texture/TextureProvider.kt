package render.texture

import render.mesh.Mesh
import render.mesh.TexturedMesh
import render.mesh.TexturedMeshIndexed

class TextureProvider(textureAtlasPath: String, atlasSize: Int) {
    private val textureAtlas = TextureAtlas(textureAtlasPath, atlasSize)

    fun activateAtlas() {
        textureAtlas.activate()
    }

    fun createAtlasMesh(positions: List<Float>, uvs: List<Float>, indices: List<Int>?): Mesh {
        if (indices == null)
            return TexturedMesh(positions, uvs, textureAtlas.texture)
        return TexturedMeshIndexed(positions, uvs, indices, textureAtlas.texture)
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