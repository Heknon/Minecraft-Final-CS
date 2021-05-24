import org.joml.Vector3f
import kotlin.math.abs

fun main() {
    println(bresenham3DWalk(
        Vector3f(0f, 0f, 0f),
        Vector3f(0f, 0f, 0f),
        6
    ))
}

fun bresenham3DWalk(origin: Vector3f, destination: Vector3f, maxDistance: Int): MutableList<List<Float>> {
    val points = mutableListOf<List<Float>>()
    points.add(listOf(origin.x, origin.y, origin.z))

    val dx = abs(destination.x - origin.x)
    val dy = abs(destination.y - origin.y)
    val dz = abs(destination.z - origin.z)

    var xs = if (destination.x > origin.x) 1 else -1
    var ys = if (destination.y > origin.y) 1 else -1
    var zs = if (destination.z > origin.z) 1 else -1

    if (dx >= dy && dx >= dz) {
        var p1 = 2 * dy - dx
        var p2 = 2 * dz - dx

        while (origin.x != destination.x) {
            origin.x += xs

            if (p1 >= 0) {
                origin.y += ys
                p1 -= 2 * dx
            }
            if (p2 >= 0) {
                origin.z += zs
                p2 -= 2 * dx
            }

            p1 += 2 * dy
            p2 += 2 * dz
            points.add(listOf(origin.x, origin.y, origin.z))
        }
    } else if (dy >= dx && dy >= dz) {
        var p1 = 2 * dx - dy
        var p2 = 2 * dz - dy

        while (origin.y != destination.y) {
            origin.y += ys

            if (p1 >= 0) {
                origin.x += xs
                p1 -= 2 * dy
            }
            if (p2 >= 0) {
                origin.z += zs
                p2 -= 2 * dy
            }

            p1 += 2 * dx
            p2 += 2 * dz
            points.add(listOf(origin.x, origin.y, origin.z))
        }
    } else {
        var p1 = 2 * dy - dz
        var p2 = 2 * dx - dz

        while (origin.z != destination.z) {
            origin.z += zs

            if (p1 >= 0) {
                origin.y += ys
                p1 -= 2 * dz
            }
            if (p2 >= 0) {
                origin.x += xs
                p2 -= 2 * dz
            }

            p1 += 2 * dy
            p2 += 2 * dx
            points.add(listOf(origin.x, origin.y, origin.z))
        }
    }

    return points
}
