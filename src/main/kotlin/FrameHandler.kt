import render.Camera
import render.Renderer
import render.texture.TextureProvider
import window.MouseInput
import window.Window
import world.*
import world.chunk.block.Block
import world.chunk.block.BlockProvider

class FrameHandler(private val window: Window, private val mouseInput: MouseInput) {
    private val camera = Camera()
    private val renderer = Renderer(camera, window)
    private val textureProvider = TextureProvider("/textures/blocks.png", 16)
    private val world = World(textureProvider)


    init {
        textureProvider.activateAtlas()
        world.generateWorld()
    }

    fun input() {
        camera.handleInput(window)
    }

    fun update(interval: Float) {
        camera.updatePositioning(mouseInput)

    }

    fun render() {
        renderer.render(window, world.chunks)
    }

    fun cleanup() {
        renderer.cleanup()
        world.cleanup()
    }
}