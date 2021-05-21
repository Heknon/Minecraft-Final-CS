package render

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import window.MouseInput
import window.Window
import kotlin.math.cos
import kotlin.math.sin

class Camera {
    private val position: Vector3f = Vector3f()
    private val rotation: Vector3f = Vector3f()
    val viewMatrix = Matrix4f()

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

        viewMatrix.rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), Vector3f(1f, 0f, 0f))
            .rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), Vector3f(0f, 1f, 0f))
        viewMatrix.translate(-position.x, -position.y, -position.z)
        return viewMatrix
    }

    fun handleInput(window: Window) {
        cameraIncrementationTracker.set(0.0, 0.0, 0.0)
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraIncrementationTracker.z = -1f
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraIncrementationTracker.z = 1f
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraIncrementationTracker.x = -1f
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraIncrementationTracker.x = 1f
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            cameraIncrementationTracker.y = -1f
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraIncrementationTracker.y = 1f
        }
    }

    fun updatePositioning(mouseInput: MouseInput) {
        move(
            cameraIncrementationTracker.x * CAMERA_POSITION_STEP,
            cameraIncrementationTracker.y * CAMERA_POSITION_STEP,
            cameraIncrementationTracker.z * CAMERA_POSITION_STEP
        )

        val rot = mouseInput.displVec
        moveRotation((rot?.x ?: 0f) * MOUSE_SENSITIVITY, (rot?.y ?: 0f) * MOUSE_SENSITIVITY, 0f)
    }

    companion object {
        const val CAMERA_POSITION_STEP = 0.15f
        const val MOUSE_SENSITIVITY = 0.4f
    }
}