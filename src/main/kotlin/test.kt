import world.Location
import world.chunk.block.BlockProvider
import java.lang.Math.floorMod

fun main() {
    val x = -43
    val z = -10
    println(getChunkStartX(x))
    println(getChunkStartX(z))
    println(getNeighboringChunks(x, z))
}

fun getChunkStartX(x: Int): Double {
    if (x < 0) {
        return (x - floorMod(x, 16)).toDouble()
    }

    return (x - x % 16).toDouble()
}

fun getChunkStartZ(z: Int): Double {
    if (z < 0) {
        return (z - floorMod(z, 16)).toDouble()
    }

    return (z - z % 16).toDouble()
}

val directions = arrayOf(
    BlockProvider.Direction.North,
    BlockProvider.Direction.East,
    BlockProvider.Direction.South,
    BlockProvider.Direction.West
)

fun getNeighboringChunks(x: Int, z: Int): MutableMap<BlockProvider.Direction, Location> {
    val neighbors = mutableMapOf<BlockProvider.Direction, Location>()

    for (direction in directions) {
        val neighborLoc = getNeighboringChunkLocation(x, z, direction)
        neighbors[direction] = neighborLoc
    }

    return neighbors
}

private fun getNeighboringChunkLocation(x: Int, z: Int, direction: BlockProvider.Direction): Location {
    var newX = getChunkStartX(x)
    var newZ = getChunkStartZ(z)

    when (direction) {
        BlockProvider.Direction.East -> newX += 16
        BlockProvider.Direction.West -> newX -= 16
        BlockProvider.Direction.South -> newZ += 16
        BlockProvider.Direction.North -> newZ -= 16
        else -> null
    }

    return Location(null, newX, 0.0, newZ)
}