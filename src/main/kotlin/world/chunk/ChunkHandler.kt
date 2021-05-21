package world.chunk

import render.texture.TextureProvider
import world.Location
import world.World
import world.chunk.block.Block
import world.chunk.block.BlockData
import world.chunk.block.BlockProvider

class ChunkHandler(val world: World, val blockProvider: BlockProvider, textureProvider: TextureProvider) {
    private val chunkFactory = ChunkFactory(textureProvider)

    fun buildChunk(location: Location, blocks: MutableList<BlockData>): Chunk {
        return chunkFactory.buildChunk(location, blocks)
    }

    fun buildChunk(x: Double, y: Double, z: Double, blocks: MutableList<BlockData>): Chunk {
        return buildChunk(Location(world, x, y, z), blocks)
    }

    fun updateBlock(x: Double, y: Double, z: Double, blockData: BlockData, chunk: Chunk) {
        chunk.replaceBlock(x, y, z, blockData)
        updateChunk(chunk)

        if (chunk.blockNeighborsChunk(x.toInt(), z.toInt())) {
            val directions = chunk.getNeighboringChunkDirectionsOfBlock(x.toInt(), z.toInt())

            println(directions)
            for (direction in directions) {
                updateChunk(world.getNeighboringChunk(x, z, direction))
            }

        }
    }

    fun updateChunk(chunk: Chunk?) {
        if (chunk == null) return
        world.replaceChunkAt(chunk.location.x, chunk.location.z, buildChunk(chunk.location, chunk.blocks.map { it.data }.toMutableList()))
    }


}