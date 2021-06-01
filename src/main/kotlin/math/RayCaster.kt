package math

import org.joml.Math.cos
import org.joml.Vector3f
import org.joml.Vector3f.distance
import org.joml.Vector4f
import render.Camera
import render.Ray
import render.Renderer
import render.Transformer
import window.Window
import world.World
import java.awt.Cursor
import java.lang.Math.toRadians
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.tan

class RayCaster(private val camera: Camera, private val window: Window, world: World) {
    private val start = camera.positionVector
    val end = camera.positionVector
    val direction = camera.rotationVector

    fun step(scale: Float) {
        step(end, scale)
    }

    fun step(resultVector: Vector3f, scale: Float) {
        val yaw = toRadians((direction.y + 90).toDouble()).toFloat()
        val pitch = toRadians(direction.x.toDouble()).toFloat()

        resultVector.x -= cos(yaw) * scale
        resultVector.z -= sin(yaw) * scale
        resultVector.y -= tan(pitch) * scale
    }

    fun getLength(): Float {
        return distance(start.x, start.y, start.z, end.x, end.y, end.z)
    }

    fun update() {
        start.set(camera.positionVector)
        end.set(camera.positionVector)
        direction.set(camera.rotationVector)
    }

    fun march(step: Float, distance: Int, stopCondition: (curr: Vector3f) -> Boolean): Vector3f? {
        val mouseRay = getRayDirection(window)

        println("START: ${start.x}, ${start.y}, ${start.z}")

        val r = end.add(Vector3f(mouseRay).mul(6.0f))
        Ray(camera.positionVector, mouseRay, 6f, camera.rotationVector)

        for (pos in bresenham3DWalk(start, r)) {
            println("POS: ${pos.x}, ${pos.y}, ${pos.z}")
            if (stopCondition(pos)) return pos
        }

        return null
    }

    fun bresenham3DWalk(origin: Vector3f, destination: Vector3f): MutableList<Vector3f> {
        val points = mutableListOf<Vector3f>()
        var x1 = origin.x.toLong()
        var y1 = origin.y.toLong()
        var z1 = origin.z.toLong()

        var x2 = destination.x.toLong()
        var y2 = destination.y.toLong()
        var z2 = destination.z.toLong()
        points.add(Vector3f(x1.toFloat(), y1.toFloat(), z1.toFloat()))

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
                points.add(Vector3f(x1.toFloat(), y1.toFloat(), z1.toFloat()))
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
                points.add(Vector3f(x1.toFloat(), y1.toFloat(), z1.toFloat()))
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
                points.add(Vector3f(x1.toFloat(), y1.toFloat(), z1.toFloat()))
            }
        }

        return points
    }

    fun peekMarch(step: Float, distance: Int, stopCondition: (curr: Vector3f, next: Vector3f) -> Boolean): Vector3f {
        while (getLength() <= distance) {
            val next = Vector3f(end)
            step(next, step)
            if (stopCondition(end, next)) break
            end.x = next.x
            end.y = next.y
            end.z = next.z
        }

        return end
    }

    fun getRayDirection(window: Window): Vector3f {
        val mouseX = window.width / 2
        val mouseY = window.height / 2

        val nx = (2f * mouseX) / window.width - 1
        val ny = (2f * mouseY) / window.height - 1

        val clipCoords = Vector4f(nx, ny, -1f, 1f)

        val invertedProjection = camera.transformer!!.getProjectionMatrix().invert()
        val eyeCoordsMat = invertedProjection.transform(clipCoords)
        val eyeCoords = Vector4f(eyeCoordsMat.x, eyeCoordsMat.y, -1f, 0f)

        val invertedView = camera.viewMatrixClone.invert()
        val rayWorld = invertedView.transform(eyeCoords)
        val mouseRay = Vector3f(rayWorld.x, rayWorld.y, rayWorld.z)
        mouseRay.normalize()

        return mouseRay
    }


}