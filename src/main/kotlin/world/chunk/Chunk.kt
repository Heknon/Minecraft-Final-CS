package world.chunk

import org.joml.Vector3f
import render.mesh.Mesh
import render.mesh.WorldObject
import utility.modulo
import world.Location
import world.chunk.block.Block
import world.chunk.block.BlockData
import world.chunk.block.BlockProvider
import java.lang.Math.floorMod

class Chunk(
    val location: Location,
    internal val blocks: MutableList<Block>,
    override var mesh: Mesh
) : WorldObject {
    override val position: Vector3f = Vector3f(location.x.toFloat(), location.y.toFloat(), location.z.toFloat())
    override val rotation: Vector3f = Vector3f()
    override val scale: Float = 1f

    /**
     * used local chunk coordinates, bottom left front is 0, 0, 0
     * top right back is chunkSizeX - 1, chunkSizeY - 1, chunkSizeZ - 1 (last coordinate)
     */
    fun localBlockAccess(x: Int, y: Int, z: Int): Block? {
        return blocks.getOrNull(getBlockIndex(x, y, z))
    }

    fun replaceBlock(x: Long, y: Long, z: Long, blockData: BlockData) {
        blocks[getBlockIndex(
            (x modulo location.world!!.chunkSizeX).toInt(),
            y.toInt(),
            (z modulo location.world.chunkSizeZ).toInt()
        )] =
            Block(Location(location.world, x.toDouble(), y.toDouble(), z.toDouble()), blockData)
    }

    fun updateBlockData(x: Long, y: Long, z: Long, blockData: BlockData) {
        blocks[getBlockIndex(
            (x modulo location.world!!.chunkSizeX).toInt(),
            y.toInt(),
            (z modulo location.world.chunkSizeZ).toInt()
        )].data = blockData
    }

    fun getBlockIndex(localX: Int, localY: Int, localZ: Int): Int {
        return localX + localZ * location.world!!.chunkSizeX + localY * location.world.chunkSizeX * location.world.chunkSizeZ
    }

}