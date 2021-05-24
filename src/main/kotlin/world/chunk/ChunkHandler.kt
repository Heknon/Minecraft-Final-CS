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


    fun remeshLocationTolerant(location: Location, chunk: Chunk?) {
        remeshChunk(chunk)

        val blockNeighbors = world.getBlockAt(location)?.getNeighboringDirections()
        if (blockNeighbors != null && blockNeighbors.isNotEmpty()) {
            for (direction in blockNeighbors.filter { it != BlockProvider.Direction.Up && it != BlockProvider.Direction.Down }) {
                remeshChunk(world.getNeighboringChunk(location.x, location.z, direction))
            }

        }
    }

    fun remeshChunk(chunk: Chunk?) {
        if (chunk == null) return
        chunk.mesh = chunkFactory.buildChunkMesh(chunk.location, chunk.blocks.map { it.data }.toMutableList()).second
    }


}