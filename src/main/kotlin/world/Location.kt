package world

import world.chunk.block.BlockProvider

data class Location(
    val world: World?,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Double = 0.0,
    val pitch: Double = 0.0
) {
    fun compress(): Long {
        return x.toLong() and 0x7FFFFFF or (z.toLong() and 0x7FFFFFF shl 27) or (y.toLong() shl 54)
    }

    fun getNeighborLocation(direction: BlockProvider.Direction): Location {
        return when (direction) {
            BlockProvider.Direction.North -> Location(world, x, y, z + 1)
            BlockProvider.Direction.South -> Location(world, x, y, z - 1)
            BlockProvider.Direction.East -> Location(world, x + 1, y, z)
            BlockProvider.Direction.West -> Location(world, x - 1, y, z)
            BlockProvider.Direction.Up -> Location(world, x, y + 1, z)
            BlockProvider.Direction.Down -> Location(world, x, y - 1, z)
            else -> throw IllegalArgumentException("Must pass real direction such as: North, South, East, West, Up, Down")
        }
    }
}

fun Long.getLocation(): Location {
    val packed = this
    val x = (packed shl 37 shr 37).toDouble()
    val y = (packed ushr 54).toDouble()
    val z = (packed shl 10 shr 37).toDouble()
    return Location(null, x, y, z)
}