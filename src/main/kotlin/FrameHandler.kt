import org.joml.Vector3f
import player.Player
import render.Renderer
import render.mesh.Mesh
import render.texture.TextureProvider
import window.Window
import world.World

class FrameHandler(val window: Window) {
    private val textureProvider = TextureProvider("/textures/blocks.png", 16)
    private val world = World(textureProvider)
    private val player = Player(
        Vector3f(0f, 15f, 0f),
        Vector3f(),
        world,
        Mesh(listOf(), listOf(), listOf(), null),
        window
    )
    private val renderer = Renderer(player.camera, window, textureProvider)


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
            val lookingAt = player.lookingAt
            println("----------------=-=--=-=")

            println(lookingAt)
            println("----------------=-=--=-=")
            lookingAt?.updateData(world.getBlockDataById(0)!!)

        } else if (window.cursor.isRightButtonPressed()) {
            player.lookingAtNeighbor?.updateData(world.getBlockDataById(3)!!)
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