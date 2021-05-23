package world.chunk

import world.World
import world.getLocation

class ChunkCreationNeighborBookkeeper(private val world: World) {
    private val neighborCreationBooks = mutableMapOf<Long, MutableList<Long>>()

    fun registerChunk(chunk: Chunk) {
        val compressedChunkLoc = chunk.location.compress()
        val neighbors = world.getNeighboringChunks(chunk.location.x, chunk.location.z)

        for (neighbor in neighbors) {
            if (neighbor.value == null) {
                val compressedNeighborLoc = neighbor.key.compress()
                if (!neighborCreationBooks.containsKey(compressedNeighborLoc)) {
                    neighborCreationBooks[compressedNeighborLoc] = mutableListOf()
                }

                neighborCreationBooks[compressedNeighborLoc]!!.add(compressedChunkLoc)
            }
        }
    }

    fun chunkBuildAlert(chunk: Chunk) {
        if (chunk.location.world == null) {
            error("Chunk location must not have a null world!")
        }

        val builtChunkLocCompressed = chunk.location.compress()
        if (neighborCreationBooks.containsKey(builtChunkLocCompressed)) {
            for (inquiredChunkUpdate in neighborCreationBooks[builtChunkLocCompressed]!!) {
                chunk.location.world.remeshChunk(chunk.location.world.getChunkAt(inquiredChunkUpdate.getLocation())!!)
            }

            neighborCreationBooks.remove(builtChunkLocCompressed)
        }
    }
}