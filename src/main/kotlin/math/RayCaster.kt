package math

import org.joml.Math.cos
import org.joml.Vector3f
import org.joml.Vector3f.distance
import render.Camera
import world.World
import java.lang.Math.toRadians
import kotlin.math.sin
import kotlin.math.tan

class RayCaster(private val camera: Camera) {
    private val start = camera.positionVector
    val end = camera.positionVector
    val direction = camera.rotationVector

    fun step(scale: Float) {
        val yaw = toRadians((direction.y + 90).toDouble()).toFloat()
        val pitch = toRadians(direction.x.toDouble()).toFloat()

        end.x -= cos(yaw) * scale
        end.z -= sin(yaw) * scale
        end.y -= tan(pitch) * scale
    }

    fun getLength(): Float {
        return distance(start.x, start.y, start.z, end.x, end.y, end.z)
    }

    fun update() {
        start.set(camera.positionVector)
        end.set(camera.positionVector)
        direction.set(camera.rotationVector)
    }

    fun march(step: Float, distance: Int, stopCondition: (end: Vector3f) -> Boolean) {
        while (getLength() <= distance) {
            if (stopCondition(end)) break
            step(step)
        }
    }


}