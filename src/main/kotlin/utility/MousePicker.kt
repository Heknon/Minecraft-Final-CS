package utility

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import render.Camera
import render.Transformer
import window.MouseInput
import window.Window

class MousePicker(
    private val window: Window,
    private val camera: Camera,
    private val transformer: Transformer
) {
    var viewMatrix = Matrix4f()
    var projectionMatrix = Matrix4f()
    var currentRay = Vector3f()

    fun update() {
        viewMatrix = camera.viewMatrixClone
        projectionMatrix = transformer.getProjectionMatrix()
        currentRay = calculateMouseRay()
    }

    fun calculateMouseRay(): Vector3f {
        val mouseX = window.width / 2f
        val mouseY = window.height / 2f
        val ndc = getNormalizedDeviceCoordinates(mouseX, mouseY)
        val clipCoordinates = Vector4f(ndc.x, ndc.y, -1f, 1f)
        val eyeCoordinates = getEyeCoordinates(clipCoordinates)
        return getWorldCoordinates(eyeCoordinates)
    }

    private fun getWorldCoordinates(eyeCoordinates: Vector4f): Vector3f {
        val invertedProjection = viewMatrix.invert()
        val worldCoordinates = invertedProjection.transform(eyeCoordinates)
        return Vector3f(eyeCoordinates.x, eyeCoordinates.y, worldCoordinates.z).normalize()
    }

    private fun getEyeCoordinates(clipCoordinates: Vector4f): Vector4f {
        val invertedProjection = projectionMatrix.invert()
        val eyeCoordinates = invertedProjection.transform(clipCoordinates)
        return Vector4f(eyeCoordinates.x, eyeCoordinates.y, -1f, 0f)
    }


    private fun getNormalizedDeviceCoordinates(x: Float, y: Float): Vector2f {
        val deviceX: Float = (2f * x) / window.width - 1f
        val deviceY: Float = (2f * y) / window.height - 1f
        return Vector2f(deviceX, deviceY)
    }
}