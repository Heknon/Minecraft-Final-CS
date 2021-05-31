package render

import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import window.Cursor
import window.Window
import kotlin.math.cos
import kotlin.math.sin

class Camera(
    val position: Vector3f = Vector3f(0f, 15f, 0f),
    val rotation: Vector3f = Vector3f(0f, 0f, 0f)
) {
    val viewMatrix = Matrix4f()
    var transformer: Transformer? = null

    val positionVector get() = Vector3f(position)
    val rotationVector get() = Vector3f(rotation)
    val viewMatrixClone get() = Matrix4f(viewMatrix)

    private val cameraIncrementationTracker = Vector3f()

    fun move(offsetX: Float, offsetY: Float, offsetZ: Float) {
        if (offsetZ != 0f) {
            position.x += sin(Math.toRadians(rotation.y.toDouble())).toFloat() * -1 * offsetZ
            position.z += cos(Math.toRadians(rotation.y.toDouble())).toFloat() * offsetZ
        }

        if (offsetX != 0f) {
            position.x += sin(Math.toRadians(rotation.y.toDouble() - 90)).toFloat() * -1 * offsetX
            position.z += cos(Math.toRadians(rotation.y.toDouble() - 90)).toFloat() * offsetX
        }

        position.y += offsetY
    }

    fun moveRotation(offsetX: Float, offsetY: Float, offsetZ: Float) {
        rotation.x += offsetX
        rotation.y += offsetY
        rotation.z += offsetZ
    }

    fun updateViewMatrix(): Matrix4f {
        viewMatrix.identity()

        viewMatrix.rotate(toRadians(rotation.x), Vector3f(1f, 0f, 0f))
            .rotate(toRadians(rotation.y), Vector3f(0f, 1f, 0f))
        viewMatrix.translate(-position.x, -position.y, -position.z)
        return viewMatrix
    }

    fun updatePositioning(interval: Float, cursor: Cursor) {
        move(
            cameraIncrementationTracker.x * CAMERA_POSITION_STEP,
            cameraIncrementationTracker.y * CAMERA_POSITION_STEP,
            cameraIncrementationTracker.z * CAMERA_POSITION_STEP
        )

        //val rot = cursor.displVec
        //moveRotation((rot?.x ?: 0f) * MOUSE_SENSITIVITY, (rot?.y ?: 0f) * MOUSE_SENSITIVITY, 0f)
    }

    companion object {
        const val CAMERA_POSITION_STEP = 0.15f
        const val MOUSE_SENSITIVITY = 0.4f
    }
}