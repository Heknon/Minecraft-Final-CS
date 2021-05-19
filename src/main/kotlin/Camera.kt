import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera {
    private val position: Vector3f = Vector3f()
    private val rotation: Vector3f = Vector3f()
    val viewMatrix = Matrix4f()

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
}