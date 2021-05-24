package world.chunk.block

import world.Location
import world.chunk.Chunk
import world.chunk.ChunkFactory

class Block(
    val location: Location,
    var data: BlockData
) {
    override fun toString(): String {
        return "Block(location: $location)"
    }

    /**
     * get neighboring block from certain direction
     * returns null if:
     *  1) chunk not found
     *  2) location out of block bounds of chunk found
     */
    fun getNeighbor(direction: BlockProvider.Direction): Block? {
        if (location.world == null) {
            error("Block ($this) missing world location!")
        }

        return location.world.getBlockAt(location.getNeighborLocation(direction))
    }

    fun getNeighboringDirections(): MutableList<BlockProvider.Direction> {
        val directions = mutableListOf<BlockProvider.Direction>()
        for (direction in ChunkFactory.faceDirections) {
            if (getNeighbor(direction) != null)
                directions.add(direction)
        }

        return directions
    }

    fun hasNeighbors(): Boolean {
        return getNeighboringDirections().isNotEmpty()
    }

    /**
     * returns null if at block location a chunk hasn't been generated
     */
    fun getOwningChunk(): Chunk? {
        return location.world?.getChunkAt(location)
    }

    fun updateData(blockData: BlockData) {
        location.world?.updateBlockData(this, blockData)
    }
}