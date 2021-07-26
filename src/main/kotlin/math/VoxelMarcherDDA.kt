package math

import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector4f
import render.Camera
import window.Window
import java.lang.Math.signum
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sign

class VoxelMarcherDDA(val cellSize: Vector3f = Vector3f(1f, 1f, 1f)) {
    fun march(camera: Camera, window: Window, distance: Float, stopCondition: (Vector3f) -> Boolean): Vector3f? {
        return marchV2(camera.positionVector, getRayDirection(camera, window), distance, stopCondition)
    }

    fun marchV3(ray: Ray, maxDistance: Float, hitCallback: () -> Boolean) {
        val p: Vector3i = Vector3i(floor(ray.origin.x).toInt(), floor(ray.origin.y).toInt(), floor(ray.origin.z).toInt())
        val d: Vector3f = ray.direction
        val step: Vector3i = Vector3i(sign(d.x).toInt(), sign(d.y).toInt(), sign(d.z).toInt())
        val tmax: Vector3i = intbound(ray.origin, d)
        val tdelta: Vector3i = step.div(d)
        val radius = maxDistance / glms

    }

    fun marchV2(start: Vector3f, direction: Vector3f, distance: Float, stopCondition: (Vector3f) -> Boolean): Vector3f? {
        var xPos = floor(start.x)
        var yPos = floor(start.y)
        var zPos = floor(start.z)

        val stepX = sign(direction.x)
        val stepY = sign(direction.y)
        val stepZ = sign(direction.z)

        val tMax = Vector3f(intbound(start.x, direction.x), intbound(start.y, direction.y), intbound(start.z, direction.z))
        val tDelta = Vector3f(stepX / direction.x, stepY / direction.y, stepZ / direction.z)

        var faceX: Float
        var faceY: Float
        var faceZ: Float


        while (true) {
            if (stopCondition(Vector3f(xPos, yPos, zPos))) {
                return Vector3f(xPos, yPos, zPos)
            }

            if (tMax.x < tMax.y) {
                if (tMax.x < tMax.z) {
                    if (tMax.x > distance) break

                    xPos += stepX
                    tMax.x += tDelta.x

                    faceX = -stepX
                    faceY = 0f
                    faceZ = 0f
                } else {
                    if (tMax.z > distance) break

                    zPos += stepZ
                    tMax.z += tDelta.z

                    faceX = 0f
                    faceY = 0f
                    faceZ = -stepZ
                }
            } else {
                if (tMax.y < tMax.z) {
                    if (tMax.y > distance) break

                    yPos += stepY
                    tMax.y += tDelta.y

                    faceX = 0f
                    faceY = -stepY
                    faceZ = 0f
                } else {
                    if (tMax.z > distance) break

                    zPos += stepZ
                    tMax.z += tDelta.z

                    faceX = 0f
                    faceY = 0f
                    faceZ = -stepZ
                }
            }
        }

        return null
    }

    fun march(start: Vector3f, direction: Vector3f, distance: Float, stopCondition: (Vector3f) -> Boolean): Vector3f? {
        val origin = Vector3f(floor(start.x), floor(start.y), floor(start.z))

        var t = 0f
        val rVector = Vector3f(direction)
        val tVector = Vector3f(
            if (rVector.x >= 0) (cellSize.x - start.x) / rVector.x else -((cellSize.x - start.x) / rVector.x),
            if (rVector.y >= 0) (cellSize.y - start.y) / rVector.y else -((cellSize.y - start.y) / rVector.y),
            if (rVector.z >= 0) (cellSize.z - start.z) / rVector.z else -((cellSize.z - start.z) / rVector.z)
        )
        val deltaTVector = Vector3f(
            cellSize.x / rVector.x,
            cellSize.y / rVector.y,
            cellSize.z / rVector.z
        )
        val currentPosition = Vector3f(origin)

        while (true) {
            if (tVector.x < tVector.y && tVector.x < tVector.z) {
                t = tVector.x

                if (rVector.x >= 0) {
                    currentPosition.x += 1
                    tVector.x += deltaTVector.x
                } else {
                    currentPosition.x -= 1
                    tVector.x -= deltaTVector.x
                }
            } else if (tVector.y < tVector.x && tVector.y < tVector.z) {
                t = tVector.y

                if (rVector.x >= 0) {
                    currentPosition.y += 1
                    tVector.y += deltaTVector.y
                } else {
                    currentPosition.y -= 1
                    tVector.y -= deltaTVector.y
                }
            } else if (tVector.z < tVector.x && tVector.z < tVector.y) {
                t = tVector.z

                if (rVector.z >= 0) {
                    currentPosition.z += 1
                    tVector.z += deltaTVector.z
                } else {
                    currentPosition.z -= 1
                    tVector.z -= deltaTVector.z
                }

            }

            println("${currentPosition.x}, ${currentPosition.y}, ${currentPosition.z}")
            if (stopCondition(currentPosition))
                return currentPosition
        }

        return null
    }

    fun getRayDirection(camera: Camera, window: Window): Vector3f {
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

    fun intbound(s: Vector3f, ds: Vector3f): Vector3i {
        val v: Vector3f = Vector3f()

        for (i in 0 until 3) {
            v.setComponent(i, (if (ds[i] > 0) ceil(s[i]) - s[i] else (s[i] - floor(s[i]))) / abs(ds[i]))
        }

        return v
    }

    fun intbound(s: Float, ds: Float): Float {
        return (if (ds > 0) ceil(s) - s else s - floor(s)) / abs(ds)
    }

    fun findFace(faceX: Float, faceY: Float, faceZ: Float) {
        
    }
}