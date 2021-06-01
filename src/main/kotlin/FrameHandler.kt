import org.joml.Vector3f
import org.joml.Vector4f
import player.Player
import render.Renderer
import render.mesh.Mesh
import render.texture.TextureProvider
import utility.Timer
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

    fun getRayDirection(window: Window): Vector4f? {
        val mouseX = window.width / 2
        val mouseY = window.height / 2

        val nx = (2f * mouseX) / window.width - 1
        val ny = (2f * mouseY) / window.height - 1

        val clipCoords = Vector4f(nx, ny, -1f, 1f)

        val invertedProjection = player.camera.transformer!!.getProjectionMatrix().invert()
        val eyeCoordsMat = invertedProjection.transform(clipCoords)
        val eyeCoords = Vector4f(eyeCoordsMat.x, eyeCoordsMat.y, -1f, 0f)

        val invertedView = player.camera.viewMatrixClone.invert()
        val rayWorld = invertedView.transform(eyeCoords)
        println("${rayWorld.x}, ${rayWorld.y}, ${rayWorld.z}")
        val mouseRay = Vector3f(rayWorld.x, rayWorld.y, rayWorld.z)
        mouseRay.normalize()

        return Vector4f(mouseRay.x, mouseRay.y, mouseRay.z, 1f)
    }

    fun fuckcsjghouidfg() {

    }

    var ran = false

    val timer = Timer()
    fun update(interval: Float) {
        player.update(interval)

        if (timer.getElapsedTime(false) > 2) {
            val b = getRayDirection(window)
            val a = world.getChunkAt(player.position.x.toDouble(), player.position.z.toDouble())?.let {
                val xyz = renderer.transformation.getProjectionMatrix()
                    .mul(
                        renderer.transformation.getModelViewMatrix(it)
                    ).transform(b)

                println("==-=-=-=-=-=")
                println("XYZ: ${xyz.x}, ${xyz.y}, ${xyz.z}")
                println("POS: ${player.position.x}, ${player.position.y}, ${player.position.z}")
            }
            timer.getElapsedTime(true)
        }

//        val block = a?.let { world.getBlockAt(it.x, -a.y, a.z) }
//        println("AAA: $block")


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