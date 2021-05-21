package world.chunk

import org.joml.Vector3f
import render.mesh.Mesh
import render.mesh.WorldObject
import utility.advanceXyzDirectionBased
import world.Location
import world.chunk.block.Block
import world.chunk.block.BlockData
import world.chunk.block.BlockProvider

class Chunk(
    val location: Location,
    internal val blocks: MutableList<Block>,
    mesh: Mesh
) : WorldObject(Vector3f(location.x.toFloat(), location.y.toFloat(), location.z.toFloat()), mesh = mesh) {
    fun accessBlock(x: Int, y: Int, z: Int): Block? {
        return blocks.getOrNull(getBlockIndex(x, y, z))
    }

    fun replaceBlock(x: Double, y: Double, z: Double, blockData: BlockData) {
        blocks[getBlockIndex((x % ChunkFactory.SIZE_X).toInt(), y.toInt(), (z % ChunkFactory.SIZE_Z).toInt())] =
            Block(x, y, z, blockData)
    }

    fun blockNeighborsChunk(x: Int, z: Int): Boolean {
        return x % ChunkFactory.SIZE_X == ChunkFactory.SIZE_X - 1 || x % ChunkFactory.SIZE_X == 0
                || z % ChunkFactory.SIZE_Z == ChunkFactory.SIZE_Z - 1 || z % ChunkFactory.SIZE_Z == 0
    }

    fun getNeighboringChunkDirectionsOfBlock(x: Int, z: Int): MutableList<BlockProvider.Direction> {
        val directionsFound = mutableListOf<BlockProvider.Direction>()
        for (direction in directions) {
            if ({ x_: Int, _: Int, z_: Int ->
                    x_ < 0 || z_ < 0 || x_ >= ChunkFactory.SIZE_X || z_ >= ChunkFactory.SIZE_Z
                }.advanceXyzDirectionBased(x % ChunkFactory.SIZE_X, 0, z % ChunkFactory.SIZE_Z, direction))
                directionsFound.add(direction)
        }

        return directionsFound
    }

    fun getBlockIndex(x: Int, y: Int, z: Int): Int {
        return x + z * ChunkFactory.SIZE_X + y * ChunkFactory.SIZE_X * ChunkFactory.SIZE_Z
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