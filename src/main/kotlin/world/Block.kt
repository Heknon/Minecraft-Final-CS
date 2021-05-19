package world

import mesh.BlockMesh
import mesh.BlockProvider
import mesh.Mesh
import texture.TextureAtlas

data class Block(
    val id: Int,
    val name: String,
    val faces: List<BlockProvider.BlockFaceMesh>,
    val collidable: Boolean
) {
    fun combineFaces(faces: Set<BlockProvider.FaceDirection>, atlas: TextureAtlas): Mesh {
        val positions = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()
        val indices = mutableListOf<Int>()

        var indexOffset = 0
        for (i in this.faces) {
            if (!faces.contains(i.faceDirection) && !faces.contains(BlockProvider.FaceDirection.All)) continue

            for (j in i.positions) {
                positions.add(j)
            }

            for (j in i.uvs) {
                uvs.add(j)
            }

            var maxIndex = 0
            for (j in i.indices) {
                if (maxIndex < j) maxIndex = j
                indices.add(indexOffset + j)
            }

            indexOffset += maxIndex + 1
        }

        return Mesh(positions.toFloatArray(), uvs.toFloatArray(), indices.toIntArray(), atlas.texture)
    }
}