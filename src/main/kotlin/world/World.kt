package world

import render.texture.TextureProvider
import utility.modulo
import world.chunk.Chunk
import world.chunk.ChunkCreationNeighborBookkeeper
import world.chunk.ChunkHandler
import world.chunk.ChunkSettings
import world.chunk.block.Block
import world.chunk.block.BlockData
import world.chunk.block.BlockProvider
import java.util.concurrent.ThreadLocalRandom

class World(textureProvider: TextureProvider) {
    private val blockProvider = BlockProvider(textureProvider)
    private val chunkHandler = ChunkHandler(this, blockProvider, textureProvider)
    private val chunksLocationMap = mutableMapOf<Long, Chunk>()
    val chunks get() = chunksLocationMap.values

    private val neighborCreationBookkeeper = ChunkCreationNeighborBookkeeper(this)

    val chunkSettings = ChunkSettings(16, 16, 16)
    val chunkSizeX get() = chunkSettings.sizeX
    val chunkSizeY get() = chunkSettings.sizeY - 5
    val chunkSizeZ get() = chunkSettings.sizeZ

    val blocks = MutableList(5) {
        MutableList(16 * 16 * 16) { _ ->
            getBlockDataById(it % 3 + 1)!!
        }
    }

    fun generateWorld() {
        val startTime = System.currentTimeMillis()
        for (z in 0 until 1) {
            for (x in 0 until 1) {
                val chunk = chunkHandler.buildChunk(
                    Location(
                        this,
                        x * chunkSizeX.toDouble(),
                        0.0,
                        z * chunkSizeZ.toDouble()
                    ), blocks[ThreadLocalRandom.current().nextInt(1, 4)]
                )
                this.chunksLocationMap[chunk.location.compress()] = chunk

                neighborCreationBookkeeper.registerChunk(chunk)
                neighborCreationBookkeeper.chunkBuildAlert(chunk)

            }
        }
        val endTime = System.currentTimeMillis() - startTime
//        println("Elapsed mesh creation time: " + endTime + "ms")

//        for (chunk in chunks) {
//            val startTime = System.currentTimeMillis()
//            chunkHandler.replaceBlock(
//                (chunk.location.x).toLong(),
//                15L, (chunk.location.z - 1).toLong(), getBlockDataById(0)!!
//            )
//            val endTime = System.currentTimeMillis() - startTime
//            println("Elapsed mesh creation time: " + endTime + "ms")
//        }
    }

    fun getBlockDataById(id: Int): BlockData? {
        return blockProvider.blocks[id]
    }

    fun updateBlockData(block: Block, data: BlockData) {
        val chunk = block.getOwningChunk()
        chunk?.updateBlockData(
            block.location.x.toLong(),
            block.location.y.toLong(),
            block.location.z.toLong(),
            data
        )
        chunkHandler.remeshLocationTolerant(block.location, chunk)
    }

    fun getBlockAt(location: Location): Block? {
        return getBlockAt(location.x, location.y, location.z)
    }

    fun getBlockAt(x: Double, y: Double, z: Double): Block? {
        return getBlockAt(x.toLong(), y.toLong(), z.toLong())
    }

    fun getBlockAt(x: Float, y: Float, z: Float): Block? {
        return getBlockAt(x.toLong(), y.toLong(), z.toLong())
    }

    fun getBlockAt(x: Long, y: Long, z: Long): Block? {
        return getChunkAt(
            x.toDouble(),
            z.toDouble()
        )?.localBlockAccess((x modulo chunkSizeX).toInt(), y.toInt(), (z modulo chunkSizeZ).toInt())
    }

    fun remeshChunk(chunk: Chunk) {
        chunkHandler.remeshChunk(chunk)
    }

    fun getNeighboringChunks(x: Double, z: Double): MutableMap<Location, Chunk?> {
        val neighbors = mutableMapOf<Location, Chunk?>()

        for (direction in directions) {
            val neighborLoc = getNeighboringChunkLocation(x, z, direction)
            neighbors[neighborLoc] = getNeighboringChunk(x, z, direction)
        }

        return neighbors
    }

    fun getNeighboringChunk(x: Double, z: Double, direction: BlockProvider.Direction): Chunk? {
        // set x and z to coordinates of beginning of chunk
        val neighborLocation = getNeighboringChunkLocation(x, z, direction)

        return getChunkAt(neighborLocation.x, neighborLocation.z)
    }

    private fun getNeighboringChunkLocation(x: Double, z: Double, direction: BlockProvider.Direction): Location {
        var newX = getChunkStartX(x.toLong())
        var newZ = getChunkStartZ(z.toLong())

        when (direction) {
            BlockProvider.Direction.East -> newX += chunkSizeX
            BlockProvider.Direction.West -> newX -= chunkSizeX
            BlockProvider.Direction.South -> newZ += chunkSizeZ
            BlockProvider.Direction.North -> newZ -= chunkSizeZ
            else -> null
        }

        return Location(this, newX, 0.0, newZ)
    }

    fun getChunkAt(x: Double, z: Double): Chunk? {
        return chunksLocationMap[getChunkIndex(x.toLong(), z.toLong())]
    }

    fun getChunkAt(location: Location): Chunk? {
        return getChunkAt(location.x, location.z)
    }

    private fun getChunkStartX(x: Long): Double {
        if (x < 0) {
            return (x - Math.floorMod(x, 16)).toDouble()
        }

        return (x - x % 16).toDouble()
    }

    private fun getChunkStartZ(z: Long): Double {
        if (z < 0) {
            return (z - Math.floorMod(z, 16)).toDouble()
        }

        return (z - z % 16).toDouble()
    }

    private fun getChunkIndex(x: Long, z: Long): Long {
        val newX = getChunkStartX(x).toLong()
        val newZ = getChunkStartZ(z).toLong()

        return (newX - newX % chunkSizeX) and 0x7FFFFFF or ((newZ - newZ % chunkSizeZ) and 0x7FFFFFF shl 27) or 0
    }

    fun cleanup() {
        for (chunk in chunksLocationMap.values) {
            chunk.mesh.cleanup()
        }
    }


    companion object {
        val directions = arrayOf(
            BlockProvider.Direction.North,
            BlockProvider.Direction.East,
            BlockProvider.Direction.South,
            BlockProvider.Direction.West
        )
    }

}