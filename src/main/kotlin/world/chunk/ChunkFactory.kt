package world.chunk

import render.mesh.Mesh
import render.texture.TextureProvider
import utility.addAllFloatArray
import utility.addAllIntArray
import world.Location
import world.World
import world.chunk.block.Block
import world.chunk.block.BlockData
import world.chunk.block.BlockProvider

class ChunkFactory(private val world: World, private val textureProvider: TextureProvider) {
    fun buildChunk(location: Location, blocks: MutableList<BlockData>): Chunk {
        val res = buildChunkMesh(location, blocks)
        return Chunk(location, res.first, res.second)
    }

    fun buildChunkMesh(
        chunkLocation: Location,
        chunkBlockData: List<BlockData>
    ): Pair<MutableList<Block>, Mesh> {
        val chunkBlocks = mutableListOf<Block>()

        val positions = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()
        val indices = mutableListOf<Int>()

        var indicesOffset = 0

        for (y in 0 until world.chunkSizeY) {
            for (z in 0 until world.chunkSizeZ) {
                for (x in 0 until world.chunkSizeX) {
                    val currChunkBlockData = localChunkBlockAccess(x, y, z, chunkBlockData)!!
                    val localBlockLocation = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                    val currChunkBlock = Block(
                        Location(
                            world,
                            chunkLocation.x + x,
                            y.toDouble(),
                            chunkLocation.z + z
                        ), currChunkBlockData
                    )
                    chunkBlocks.add(currChunkBlock)

                    if (currChunkBlock.data.id == 0) continue // if block is air skip checks

                    for (direction in faceDirections) {
                        val facingLocation = localBlockLocation.getNeighborLocation(direction)
                        val facingChunk = world.getChunkAt(facingLocation)
                        val facingBlockData =
                            world.getBlockAt(currChunkBlock.location.getNeighborLocation(direction))?.data
                                ?: localChunkBlockAccess(
                                    facingLocation.x.toInt(),
                                    facingLocation.y.toInt(),
                                    facingLocation.z.toInt(),
                                    chunkBlockData
                                )

                        val currChunkBlockFace = currChunkBlockData.faces[direction]!!

                        val facingChunkStartX = world.getChunkStartX(facingLocation.x.toLong())
                        val facingChunkStartZ = world.getChunkStartZ(facingLocation.z.toLong())
                        if (facingChunk == null
                            && (chunkLocation.x != facingChunkStartX
                                    || chunkLocation.z != facingChunkStartZ)
                        ) {
                            val compressed = Location(world, facingChunkStartX, 0.0, facingChunkStartZ).compress()

                            if (!world.neighborNeededBookkeeper.containsKey(compressed))
                                world.neighborNeededBookkeeper[compressed] = mutableSetOf()

                            if (!world.neighborNeededBookkeeper[compressed]!!.contains(chunkLocation.compress())) {
                                world.neighborNeededBookkeeper[compressed]
                                    ?.add(chunkLocation.compress())
                            }

                        }

                        // when this if statement is true `currChunkBlockFace` is added to chunk mesh
                        if (facingBlockData == null || facingBlockData.id == 0) {
                            val pos = FloatArray(currChunkBlockFace.positions.size) {
                                return@FloatArray when {
                                    it % 3 == 0 -> {
                                        // x coordinate
                                        currChunkBlockFace.positions[it] + x
                                    }
                                    it % 3 == 1 -> {
                                        // y coordinate
                                        currChunkBlockFace.positions[it] + y
                                    }
                                    it % 3 == 2 -> {
                                        // z coordinate
                                        currChunkBlockFace.positions[it] + z
                                    }
                                    else -> 0f
                                }
                            }

                            var maxIndex = 0
                            val idx = IntArray(currChunkBlockFace.indices.size) {
                                if (maxIndex < currChunkBlockFace.indices[it]) maxIndex = currChunkBlockFace.indices[it]
                                currChunkBlockFace.indices[it] + indicesOffset
                            }
                            indicesOffset += maxIndex + 1

                            positions.addAllFloatArray(pos)
                            uvs.addAllFloatArray(currChunkBlockFace.uvs)
                            indices.addAllIntArray(idx)
                        }
                    }
                }
            }
        }

        return Pair(chunkBlocks, textureProvider.createAtlasMesh(positions, uvs, indices))
    }

    /**
     * Given local chunk coordinates, returns whether or not they are in the bounds of the chunk and
     * have a valid index in chunk blocks list
     */
    fun localChunkCoordinatesOutOfBoundsCheck(localX: Int, localY: Int, localZ: Int): Boolean {
        return localX < 0 || localX >= world.chunkSizeX
                || localY < 0 || localY >= world.chunkSizeY
                || localZ < 0 || localZ >= world.chunkSizeZ
    }

    /**
     * given local chunk coordinates and list of chunk blocks, accesses chunk blocks
     * and returns block data at x y z
     * null if coordinates are out of bounds.
     */
    fun localChunkBlockAccess(localX: Int, localY: Int, localZ: Int, blocks: List<BlockData>): BlockData? {
        if (localChunkCoordinatesOutOfBoundsCheck(localX, localY, localZ)) {
            return null
        }

        return blocks.getOrNull(localX + localZ * world.chunkSizeX + localY * world.chunkSizeX * world.chunkSizeZ)
    }

    companion object {
        val faceDirections = BlockProvider.Direction.values().dropLast(1)
    }
}