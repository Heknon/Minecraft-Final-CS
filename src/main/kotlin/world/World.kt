package world

import render.texture.TextureProvider
import utility.modulo
import world.chunk.Chunk
import world.chunk.ChunkHandler
import world.chunk.ChunkSettings
import world.chunk.block.Block
import world.chunk.block.BlockData
import world.chunk.block.BlockProvider
import java.math.BigInteger
import java.util.concurrent.ThreadLocalRandom

class World(textureProvider: TextureProvider) {
    private val blockProvider = BlockProvider(textureProvider)
    private val chunkHandler = ChunkHandler(this, blockProvider, textureProvider)
    private val chunksLocationMap = mutableMapOf<Long, Chunk>()
    val chunks get() = chunksLocationMap.values

    val neighborNeededBookkeeper = mutableMapOf<Long, MutableSet<Long>>()


    val chunkSettings = ChunkSettings(16, 16, 16)
    val chunkSizeX get() = chunkSettings.sizeX
    val chunkSizeY get() = chunkSettings.sizeY
    val chunkSizeZ get() = chunkSettings.sizeZ

    val blocks = MutableList(5) {
        MutableList(16 * 16 * 16) { _ ->
            getBlockDataById(it % 3 + 1)!!
        }
    }

    fun generateWorld() {
        for (z in 0 until 2) {
            for (x in 0 until 2) {
                val startTime = System.currentTimeMillis()
                val chunk = chunkHandler.buildChunk(
                    Location(
                        this,
                        x * chunkSizeX.toDouble(),
                        0.0,
                        z * chunkSizeZ.toDouble()
                    ), blocks[ThreadLocalRandom.current().nextInt(1, 4)]
                )
                this.chunksLocationMap[chunk.location.compress()] = chunk

                val neighbors = neighborNeededBookkeeper[chunk.location.compress()]
                if (neighbors != null) {
                    for (neighbor in neighbors) {
                        chunkHandler.updateChunk(getChunkAt(neighbor.getLocation()))
                    }

                    neighborNeededBookkeeper[chunk.location.compress()] = mutableSetOf()
                }

                val endTime = System.currentTimeMillis() - startTime
                //println("Elapsed mesh creation time: " + endTime + "ms")
            }
        }

        for (chunk in chunks) {
            val startTime = System.currentTimeMillis()
            chunkHandler.replaceBlock(
                (chunk.location.x).toLong(),
                15L, (chunk.location.z - 1).toLong(), getBlockDataById(0)!!
            )
            val endTime = System.currentTimeMillis() - startTime
            println("Elapsed mesh creation time: " + endTime + "ms")
        }
    }

    fun getBlockAt(location: Location): Block? {
        return getBlockAt(location.x.toLong(), location.y.toLong(), location.z.toLong())
    }

    fun getBlockAt(x: Long, y: Long, z: Long): Block? {
        return getChunkAt(
            x.toDouble(),
            z.toDouble()
        )?.localBlockAccess((x modulo chunkSizeX).toInt(), y.toInt(), (z modulo chunkSizeZ).toInt())
    }

    fun getNeighboringChunk(location: Location, direction: BlockProvider.Direction): Chunk? {
        return getNeighboringChunk(location.x, location.z, direction)
    }

    fun getNeighboringChunk(x: Double, z: Double, direction: BlockProvider.Direction): Chunk? {
        // set x and z to coordinates of beginning of chunk
        var newX = x - x % chunkSizeX
        var newZ = z - z % chunkSizeZ

        when (direction) {
            BlockProvider.Direction.South -> newZ -= chunkSizeZ
            BlockProvider.Direction.North -> newZ += chunkSizeZ
            BlockProvider.Direction.East -> newX += chunkSizeX
            BlockProvider.Direction.West -> newX -= chunkSizeX
            else -> throw IllegalArgumentException("Must pass one of the 4 directions: North, East, South, West")
        }

        return getChunkAt(newX, newZ)
    }

    internal fun replaceChunkAt(x: Double, z: Double, chunk: Chunk) {
        chunksLocationMap[getChunkIndex(x.toLong(), z.toLong())] = chunk
    }

    fun getChunkAt(x: Double, z: Double): Chunk? {
        return chunksLocationMap[getChunkIndex(x.toLong(), z.toLong())]
    }

    fun getChunkAt(location: Location): Chunk? {
        return getChunkAt(location.x, location.z)
    }

    fun getChunkStartCoordinates(x: Double, z: Double): Location {
        return getChunkAt(x, z)?.location ?: Location(
            this,
            x - x % chunkSizeX,
            0.0,
            z - z % chunkSizeZ
        )
    }

    fun getBlockDataById(id: Int): BlockData? {
        return blockProvider.blocks[id]
    }

    fun getChunkStartX(x: Long): Double {
        var newX = x
        if (x < 0) {
            newX -= 16
        }

        return newX.toDouble()
    }

    fun getChunkStartZ(z: Long): Double {
        var newZ = z

        if (z < 0) {
            newZ -= 17
        }

        return newZ.toDouble()
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

}