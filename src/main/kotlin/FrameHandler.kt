import mesh.BlockMesh
import mesh.BlockProvider
import mesh.WorldObject
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import texture.TextureAtlas

class FrameHandler(private val window: Window, private val mouseInput: MouseInput) {
    private val textureAtlas = TextureAtlas("/textures/blocks.png", 16)
    private val blockProvider = BlockProvider(textureAtlas)
    private val camera = Camera()
    private val cameraInc = Vector3f()
    private val cameraPosStep = 0.15f
    private val mouseSensitivity = 0.4f
    private val transformation: Transformation = Transformation(window, camera)
    private val renderer = Renderer(transformation, camera)

    val objs = List(6) {
        WorldObject(
            mesh = blockProvider.blocks[it % 4]!!.combineFaces(setOf(BlockProvider.FaceDirection.All), textureAtlas),
            position = Vector3f(0f, it.toFloat(), -5f),
            rotation = Vector3f(0f, 0f, 0f),
            scale = 1f
        )
    }

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

        val rot = mouseInput.displVec
        camera.moveRotation((rot?.x ?: 0f) * mouseSensitivity, (rot?.y ?: 0f) * mouseSensitivity, 0f)

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