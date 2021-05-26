import org.joml.Vector3f
import kotlin.math.abs

fun main() {
    println(bresenham3DWalk(
        Vector3f(0f, 0f, 0f),
        Vector3f(0f, 0f, 0f)
    ))
}

fun bresenham3DWalk(origin: Vector3f, destination: Vector3f): MutableList<List<Long>> {
    val points = mutableListOf<List<Long>>()
    var x1 = origin.x.toLong()
    var y1 = origin.y.toLong()
    var z1 = origin.z.toLong()

    var x2 = destination.x.toLong()
    var y2 = destination.y.toLong()
    var z2 = destination.z.toLong()
    points.add(listOf(x1, y1, z1))

    val dx = abs(destination.x - origin.x)
    val dy = abs(destination.y - origin.y)
    val dz = abs(destination.z - origin.z)

    var xs = if (destination.x > origin.x) 1 else -1
    var ys = if (destination.y > origin.y) 1 else -1
    var zs = if (destination.z > origin.z) 1 else -1

    if (dx >= dy && dx >= dz) {
        var p1 = 2 * dy - dx
        var p2 = 2 * dz - dx

        while (x1 != x2) {
            x1 += xs

            if (p1 >= 0) {
                y1 += ys
                p1 -= 2 * dx
            }
            if (p2 >= 0) {
                z1 += zs
                p2 -= 2 * dx
            }

            p1 += 2 * dy
            p2 += 2 * dz
            points.add(listOf(x1, y1, z1))
        }
    } else if (dy >= dx && dy >= dz) {
        var p1 = 2 * dx - dy
        var p2 = 2 * dz - dy

        while (y1 != y2) {
            y1 += ys

            if (p1 >= 0) {
                x1 += xs
                p1 -= 2 * dy
            }
            if (p2 >= 0) {
                z1 += zs
                p2 -= 2 * dy
            }

            p1 += 2 * dx
            p2 += 2 * dz
            points.add(listOf(x1, y1, z1))
        }
    } else {
        var p1 = 2 * dy - dz
        var p2 = 2 * dx - dz

        while (z1 != z2) {
            z1 += zs

            if (p1 >= 0) {
                y1 += ys
                p1 -= 2 * dz
            }
            if (p2 >= 0) {
                x1 += xs
                p2 -= 2 * dz
            }

            p1 += 2 * dy
            p2 += 2 * dx
            points.add(listOf(x1, y1, z1))
        }
    }

    return points
}
