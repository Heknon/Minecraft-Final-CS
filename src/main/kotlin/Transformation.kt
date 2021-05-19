import mesh.WorldObject
import org.joml.Matrix4f

class Transformation(private val window: Window, private val camera: Camera) {
    private val projectionMatrix = Matrix4f()
    private val modelView = Matrix4f()

    private val fov = Math.toRadians(60.0).toFloat()
    private val zNear = 0.01f
    private val zFar = 1000f

    fun getProjectionMatrix(): Matrix4f {
        return projectionMatrix.setPerspective(fov, window.width / window.height.toFloat(), zNear, zFar)
    }

    fun getModelViewMatrix(obj: WorldObject): Matrix4f {
        modelView.identity().translation(obj.position)
            .rotateX(Math.toRadians(-obj.rotation.x.toDouble()).toFloat())
            .rotateY(Math.toRadians(-obj.rotation.y.toDouble()).toFloat())
            .rotateZ(Math.toRadians(-obj.rotation.z.toDouble()).toFloat())
            .scale(obj.scale)
        return Matrix4f(camera.viewMatrix).mul(modelView)
    }
}