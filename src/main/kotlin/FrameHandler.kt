import mesh.BlockMesh
import mesh.WorldObject
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class FrameHandler(private val window: Window, private val mouseInput: MouseInput) {
    private val camera = Camera()
    private val cameraInc = Vector3f()
    private val cameraPosStep = 0.15f
    private val mouseSensitivity = 0.4f
    private val transformation: Transformation = Transformation(window, camera)
    private val renderer = Renderer(transformation, camera)

    val mesh = BlockMesh.constructMesh()
    val obj = WorldObject(mesh = mesh, position = Vector3f(0f, 0f, -5f), rotation = Vector3f(0f, 0f, 0f), scale=1f)
    val objs = listOf(obj)

    fun input() {
        cameraInc.set(0.0, 0.0, 0.0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1f
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1f
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1f
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1f
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -1f
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            cameraInc.y = 1f
        }
    }

    fun update(interval: Float) {
        camera.move(cameraInc.x * cameraPosStep, cameraInc.y * cameraPosStep, cameraInc.z * cameraPosStep)

        if (mouseInput.isRightButtonPressed()) {
            val rot = mouseInput.displVec
            camera.moveRotation((rot?.x ?: 0f) * mouseSensitivity, (rot?.y ?: 0f) * mouseSensitivity, 0f)
        }
    }

    fun render() {
        renderer.render(window, objs)
    }

    fun cleanup() {
        renderer.cleanup()

        for (obj in objs) {
            obj.mesh.cleanup()
        }
    }
}