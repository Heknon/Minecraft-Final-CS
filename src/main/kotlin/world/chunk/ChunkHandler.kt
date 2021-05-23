package world.chunk

import render.texture.TextureProvider
import world.Location
import world.World
import world.chunk.block.BlockData
import world.chunk.block.BlockProvider

class ChunkHandler(val world: World, val blockProvider: BlockProvider, textureProvider: TextureProvider) {
    private val chunkFactory = ChunkFactory(world, textureProvider)

    fun buildChunk(location: Location, blocks: MutableList<BlockData>): Chunk {
        return chunkFactory.buildChunk(location, blocks)
    }

    fun buildChunk(x: Double, y: Double, z: Double, blocks: MutableList<BlockData>): Chunk {
        return buildChunk(Location(world, x, y, z), blocks)
    }

    fun replaceBlock(x: Long, y: Long, z: Long, blockData: BlockData) {
        val chunk = world.getChunkAt(x.toDouble(), z.toDouble())
        chunk?.replaceBlock(x, y, z, blockData)
        remeshChunk(chunk)

        val blockNeighbors = world.getBlockAt(x, y, z)?.getNeighboringDirections()
        if (blockNeighbors != null && blockNeighbors.isNotEmpty()) {
            for (direction in blockNeighbors.filter { it != BlockProvider.Direction.Up && it != BlockProvider.Direction.Down }) {
                remeshChunk(world.getNeighboringChunk(x.toDouble(), z.toDouble(), direction))
            }

        }
    }

    fun remeshChunk(chunk: Chunk?) {
        if (chunk == null) return
        chunk.mesh = chunkFactory.buildChunkMesh(chunk.location, chunk.blocks.map { it.data }.toMutableList()).second
    }


}