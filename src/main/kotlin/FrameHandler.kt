import math.RayCaster
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.glfwSetCursorPos
import player.Player
import render.Camera
import render.Renderer
import render.mesh.Mesh
import render.texture.TextureProvider
import window.Cursor
import window.Window
import world.World

class FrameHandler(val window: Window) {
    private val textureProvider = TextureProvider("/textures/blocks.png", 16)
    private val world = World(textureProvider)
    private val player = Player(
        Vector3f(0f, 15f, 0f),
        Vector3f(),
        world,
        Mesh(listOf(), listOf(), listOf(), null)

    )
    private val renderer = Renderer(player.camera, window)


    init {
        textureProvider.activateAtlas()
        world.generateWorld()
    }

    fun input() {
        player.handleInput(window)
    }

    var ran = false
    fun update(interval: Float) {
        player.update(interval)

        if (window.cursor.isLeftButtonPressed() && !ran) {
            //ran = true
            val block = player.lookingAt
        } else if (window.cursor.isRightButtonPressed()) {
            val block = player.lookingAt
        }

    }

    fun render() {
        renderer.render(window, world.chunks)
    }

    fun cleanup() {
        renderer.cleanup()
        world.cleanup()
    }
}