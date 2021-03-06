package utility

import com.fasterxml.jackson.databind.ObjectMapper
import org.joml.Vector3f
import world.Location
import world.World
import world.chunk.block.BlockProvider
import java.io.InputStream
import java.lang.Math.floorMod
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.*


fun String.loadResource(): String {
    return try {
        val inp: InputStream = javaClass.getResourceAsStream(this)
        val scanner = Scanner(inp, java.nio.charset.StandardCharsets.UTF_8.name())
        scanner.useDelimiter("\\A").next()
    } catch (ex: Exception) {
        ""
    }
}

fun String.loadResourceAsByteBuffer(): ByteBuffer {
    val imgBytes = javaClass.getResourceAsStream(this).readBytes()
    val buf = ByteBuffer.wrap(imgBytes)
    return buf
}

val objMapper = ObjectMapper()

fun String.readResourceAsJson(): Map<*, *> {
    val map: Map<*, *> = objMapper.readValue(javaClass.getResourceAsStream(this), MutableMap::class.java)
    return map
}

fun MutableList<Float>.addAllFloatArray(array: FloatArray) {
    for (i in array) {
        this.add(i)
    }
}

fun MutableList<Int>.addAllIntArray(array: IntArray) {
    for (i in array) {
        this.add(i)
    }
}

fun FloatBuffer.putList(floats: List<Float>): FloatBuffer {
    for (float in floats) {
        this.put(float)
    }
    return this
}

fun IntBuffer.putList(ints: List<Int>): IntBuffer {
    for (int in ints) {
        this.put(int)
    }
    return this
}

fun <T> ((Long, Long, Long) -> T).advanceXyzDirectionBased(
    x: Long,
    y: Long,
    z: Long,
    direction: BlockProvider.Direction,
    manipulation: ((Long) -> Long)? = null
): T {
    var newX = x
    var newY = y
    var newZ = z
    when (direction) {
        BlockProvider.Direction.South -> newZ = manipulation?.invoke(z) ?: z - 1
        BlockProvider.Direction.North -> newZ = manipulation?.invoke(z) ?: z + 1
        BlockProvider.Direction.East -> newX = manipulation?.invoke(x) ?: x + 1
        BlockProvider.Direction.West -> newX = manipulation?.invoke(x) ?: x - 1
        BlockProvider.Direction.Up -> newY = manipulation?.invoke(y) ?: y + 1
        BlockProvider.Direction.Down -> newY = manipulation?.invoke(y) ?: y - 1
        else -> throw IllegalArgumentException("Invalid Direction type.")
    }

    return this(newX, newY, newZ)
}

infix fun Long.modulo(size: Int): Long {
    return floorMod(this, size.toLong())
}

infix fun Int.modulo(size: Int): Int {
    return floorMod(this, size)
}

fun Vector3f.toLocation(world: World? = null): Location {
    return Location(world, this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}