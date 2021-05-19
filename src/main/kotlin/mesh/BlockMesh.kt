package mesh

import Texture

class BlockMesh {
    enum class FaceDirection {
        Top,
        Bottom,
        Left,
        Right,
        Front,
        Back
    }

    class BlockFaceMesh(
        val positions: FloatArray,
        val uvs: FloatArray,
        val indices: IntArray,
        val faceDirection: FaceDirection
    )

    companion object {
        val top = BlockFaceMesh(
            floatArrayOf(
                -0.5f, 0.5f, -0.5f, // left top back
                -0.5f, 0.5f, 0.5f, // left top front
                0.5f, 0.5f, 0.5f, // right top front
                0.5f, 0.5f, -0.5f, // right top back
            ),
            floatArrayOf(
                0.0f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.5f, 0.5f,
            ),
            intArrayOf(
                0, 1, 3, 3, 1, 2
            ),
            FaceDirection.Top
        )

        val bottom = BlockFaceMesh(
            floatArrayOf(
                -0.5f, -0.5f, -0.5f, // left bottom back
                -0.5f, -0.5f, 0.5f, // left bottom front
                0.5f, -0.5f, 0.5f, // right bottom front
                0.5f, -0.5f, -0.5f, // right bottom back
            ),
            floatArrayOf(
                0.5f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
                1.0f, 0.0f,
            ),
            intArrayOf(
                0, 1, 3, 3, 1, 2
            ),
            FaceDirection.Bottom
        )

        val left = BlockFaceMesh(
            floatArrayOf(
                -0.5f, 0.5f, 0.5f, // left top front
                -0.5f, -0.5f, 0.5f, // left bottom front
                -0.5f, -0.5f, -0.5f, // left bottom back
                -0.5f, 0.5f, -0.5f, // left top back
            ),
            floatArrayOf(
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
            ),
            intArrayOf(
                0, 1, 3, 3, 1, 2
            ),
            FaceDirection.Left
        )


        val right = BlockFaceMesh(
            floatArrayOf(
                0.5f, 0.5f, 0.5f, // right top front
                0.5f, -0.5f, 0.5f, // right bottom front
                0.5f, -0.5f, -0.5f, // right bottom back
                0.5f, 0.5f, -0.5f, // right top back

            ),
            floatArrayOf(
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
            ),
            intArrayOf(
                0, 1, 3, 3, 1, 2
            ),
            FaceDirection.Right
        )

        val front = BlockFaceMesh(
            floatArrayOf(
                -0.5f, 0.5f, 0.5f, // left top front
                -0.5f, -0.5f, 0.5f, // left bottom front
                0.5f, -0.5f, 0.5f, // right bottom front
                0.5f, 0.5f, 0.5f, // right top front
            ),
            floatArrayOf(
                1 / 16f, 0f,
                1 / 16f, 1 / 16f,
                2 / 16f, 1 / 16f,
                2 / 16f, 0f,
            ),
            intArrayOf(
                0, 1, 3, 3, 1, 2
            ),
            FaceDirection.Front
        )

        val back = BlockFaceMesh(
            floatArrayOf(
                -0.5f, 0.5f, -0.5f, // left top back 4
                -0.5f, -0.5f, -0.5f, // left bottom back 5
                0.5f, -0.5f, -0.5f, // right bottom back 6
                0.5f, 0.5f, -0.5f, // right top back 7
            ),
            floatArrayOf(
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
            ),
            intArrayOf(
                0, 1, 3, 3, 1, 2
            ),
            FaceDirection.Back
        )

        private val texture: Texture = Texture("/textures/blocks.png")

        private fun combineBlockFaces(vararg blockFaceMesh: BlockFaceMesh): Mesh {
            val positions = mutableListOf<Float>()
            val uvs = mutableListOf<Float>()
            val indices = mutableListOf<Int>()

            var indexOffset = 0
            for (i in blockFaceMesh) {
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

            return Mesh(positions.toFloatArray(), uvs.toFloatArray(), indices.toIntArray(), texture)
        }

        fun constructMesh(): Mesh {
            return combineBlockFaces(top, bottom, left, right, front, back)
        }
    }
}