package world

import render.texture.TextureProvider
import utility.advanceXyzDirectionBased
import world.chunk.Chunk
import world.chunk.ChunkFactory
import world.chunk.ChunkHandler
import world.chunk.block.Block
import world.chunk.block.BlockData
import world.chunk.block.BlockProvider
import java.util.concurrent.ThreadLocalRandom

class World(textureProvider: TextureProvider) {
    private val blockProvider = BlockProvider(textureProvider)
    private val chunkHandler = ChunkHandler(this, blockProvider, textureProvider)
    private val chunksLocationMap = mutableMapOf<Long, Chunk>()
    val chunks get() = chunksLocationMap.values

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
                        x * ChunkFactory.SIZE_X.toDouble(),
                        0.0,
                        z * ChunkFactory.SIZE_Z.toDouble()
                    ), blocks[ThreadLocalRandom.current().nextInt(1, 4)]
                )
                val endTime = System.currentTimeMillis() - startTime
                //println("Elapsed mesh creation time: " + endTime + "ms")
                this.chunksLocationMap[chunk.location.compress()] = chunk
            }
        }

//        for (chunk in chunks) {
//            chunkHandler.updateBlock(chunk.location.x - 2, 15.0, chunk.location.z - 2, getBlockDataById(0)!!, chunk)
//        }

    }

    fun getBlockAt(x: Double, y: Double, z: Double): Block? {
        return getChunkAt(x, z)?.accessBlock(
            (x % ChunkFactory.SIZE_X).toInt(),
            y.toInt(),
            (z % ChunkFactory.SIZE_Z).toInt()
        )
    }

    /**
     * assumes chunk exists
     */
    fun blockNeighborsChunkFromDirection(x: Int, z: Int, direction: BlockProvider.Direction): Boolean {
        return { x_: Int, _: Int, z_: Int ->
            x_ < 0 || z_ < 0 || x_ >= ChunkFactory.SIZE_X || z_ >= ChunkFactory.SIZE_Z
        }.advanceXyzDirectionBased(x, 0, z, direction)
    }

    fun getNeighboringChunk(x: Double, z: Double, direction: BlockProvider.Direction): Chunk? {
        // set x and z to coordinates of beginning of chunk
        var newX = x - x % ChunkFactory.SIZE_X
        var newZ = z - z % ChunkFactory.SIZE_Z

        when (direction) {
            BlockProvider.Direction.South -> newZ -= ChunkFactory.SIZE_Z
            BlockProvider.Direction.North -> newZ += ChunkFactory.SIZE_Z
            BlockProvider.Direction.East -> newX += ChunkFactory.SIZE_X
            BlockProvider.Direction.West -> newX -= ChunkFactory.SIZE_X
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
            x - x % ChunkFactory.SIZE_X,
            0.0,
            z - z % ChunkFactory.SIZE_Z
        )
    }

    fun getBlockDataById(id: Int): BlockData? {
        return blockProvider.blocks[id]
    }


    fun getChunkIndex(x: Long, z: Long): Long {
        return (x - x % ChunkFactory.SIZE_X) and 0x7FFFFFF or ((z - z % ChunkFactory.SIZE_Z) and 0x7FFFFFF shl 27) or 0
    }

    fun cleanup() {
        for (chunk in chunksLocationMap.values) {
            chunk.mesh.cleanup()
        }
    }


}