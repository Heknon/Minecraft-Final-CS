package world.chunk

import render.mesh.Mesh
import render.texture.TextureProvider
import utility.addAllFloatArray
import utility.addAllIntArray
import utility.advanceXyzDirectionBased
import world.Location
import world.chunk.block.Block
import world.chunk.block.BlockData
import world.chunk.block.BlockProvider
import kotlin.math.floor

class ChunkFactory(private val textureProvider: TextureProvider) {
    /*
    create dictionary for chunks that had no neighboring chunk so that when its neighboring chunk is added
    we could remove the un neeeded faces.
     */


    fun buildChunk(location: Location, blocks: MutableList<BlockData>): Chunk {
        val res = buildChunkMesh(location, blocks)
        return Chunk(location, res.first, res.second)
    }

    private fun buildChunkMesh(location: Location, blockDatas: List<BlockData>): Pair<MutableList<Block>, Mesh> {
        val neighboringChunks = mutableMapOf<BlockProvider.Direction, Chunk?>()
        val blocks = mutableListOf<Block>()

        for (direction in arrayOf(
            BlockProvider.Direction.North,
            BlockProvider.Direction.East,
            BlockProvider.Direction.South,
            BlockProvider.Direction.West
        )) {
            neighboringChunks[direction] = location.world!!.getNeighboringChunk(location.x, location.z, direction)
        }

        val positions = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()
        val indices = mutableListOf<Int>()

        var indexOffset = 0
        var id = 0

        for (y in 0 until SIZE_Y) {
            for (z in 0 until SIZE_Z) {
                for (x in 0 until SIZE_X) {
                    val currBlockData = accessBlock(x, y, z, blockDatas)!!
                    val currBlock = Block(location.x + x, y.toDouble(), location.z + z, currBlockData)
                    blocks.add(currBlock)
                    if (currBlock.data.id == 0) continue
                    for (direction in faceDirections) {
                        var sharedBlockData = { x_: Int, y_: Int, z_: Int ->
                            accessBlock(x_, y_, z_, blockDatas)
                        }.advanceXyzDirectionBased(x, y, z, direction)

                        val face =
                            currBlockData.faces[direction] ?: error("Failed to find ${direction.name} face")

                        // if sharedBlock is null or air add face of currBlock in current direction
                        if (sharedBlockData == null || sharedBlockData.id == 0) {
                            /*
                             when shared block is null there it means the index given the accessBlock was out of bounds
                             this means that it a neighboring block may lay in a neighboring chunk.
                             we will see if our chunk has a neighboring chunk in current direction, if not, add face
                             if it does, check if block next to current block in current direction exists, if not, add face
                             */
                            if (sharedBlockData == null &&
                                x == SIZE_X - 1 || x == 0 || z == SIZE_Z - 1 || z == 0
                            ) {
                                // see if block belongs to another neighboring chunk, if it does remove face since it is covered.
                                val neighboringChunk = neighboringChunks[direction]
                                if (neighboringChunk != null
                                    && location.world!!.blockNeighborsChunkFromDirection(x, z, direction)
                                ) {

                                    // need to reset whichever the direction is advanced to to 0
                                    val sharedBlockChunk = { x_: Int, y_: Int, z_: Int ->
                                        println(currBlock)
                                        location.world.getBlockAt(
                                            x_.toDouble(), y_.toDouble(), z_.toDouble()
                                        )
                                    }.advanceXyzDirectionBased(
                                        (location.x + x).toInt(), y, (location.z + z).toInt(), direction
                                    )
                                    println(sharedBlockChunk)
                                    println("---------------------")

                                    if (sharedBlockChunk?.data?.id != 0) {
                                        continue
                                    }
                                }
                            }

                            val pos = FloatArray(face.positions.size) {
                                return@FloatArray when {
                                    it % 3 == 0 -> {
                                        // x coordinate
                                        face.positions[it] + x
                                    }
                                    it % 3 == 1 -> {
                                        // y coordinate
                                        face.positions[it] + y
                                    }
                                    it % 3 == 2 -> {
                                        // z coordinate
                                        face.positions[it] + z
                                    }
                                    else -> 0f
                                }
                            }

                            var maxIndex = 0
                            val idx = IntArray(face.indices.size) {
                                if (maxIndex < face.indices[it]) maxIndex = face.indices[it]
                                face.indices[it] + indexOffset
                            }
                            indexOffset += maxIndex + 1

                            positions.addAllFloatArray(pos)
                            uvs.addAllFloatArray(face.uvs)
                            indices.addAllIntArray(idx)
                        }
                    }
                }
            }
        }

        return Pair(blocks, textureProvider.createAtlasMesh(positions, uvs, indices))
    }


    companion object {
        const val SIZE_X = 16
        const val SIZE_Y = 16
        const val SIZE_Z = 16

        fun accessBlock(x: Int, y: Int, z: Int, blocks: List<BlockData>): BlockData? {
            if (x < 0 || x >= SIZE_X || y < 0 || y >= SIZE_Y || z < 0 || z >= SIZE_Z) {
                return null
            }

            return blocks.getOrNull(x + z * SIZE_X + y * SIZE_X * SIZE_Z)
        }

        fun xyzToIndex(x: Int, y: Int, z: Int): Int {
            return x + z * SIZE_X + y * SIZE_X * SIZE_Z
        }

        fun indexToXyz(index: Int): IntArray {
            return intArrayOf(index % 16, floor(index / 256f).toInt(), floor(index / 16f).toInt() % 16)
        }

        private val faceDirections = BlockProvider.Direction.values().dropLast(1)
        private val blockAccessByDirection:
                Map<BlockProvider.Direction, (Int, Int, Int, List<BlockData>) -> BlockData?> = mapOf(
            Pair(BlockProvider.Direction.Up) { x, y, z, blocks ->
                return@Pair accessBlock(x, y + 1, z, blocks)
            },
            Pair(BlockProvider.Direction.Down) { x, y, z, blocks ->
                return@Pair accessBlock(x, y - 1, z, blocks)
            },
            Pair(BlockProvider.Direction.West) { x, y, z, blocks ->
                return@Pair accessBlock(x - 1, y, z, blocks)
            },
            Pair(BlockProvider.Direction.East) { x, y, z, blocks ->
                return@Pair accessBlock(x + 1, y, z, blocks)
            },
            Pair(BlockProvider.Direction.North) { x, y, z, blocks ->
                return@Pair accessBlock(x, y, z + 1, blocks)
            },
            Pair(BlockProvider.Direction.South) { x, y, z, blocks ->
                return@Pair accessBlock(x, y, z - 1, blocks)
            }
        )
    }
}