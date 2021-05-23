import org.lwjgl.Version
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.opengl.GL11.*
import utility.Timer
import window.MouseInput
import window.Window

fun main() {
    val main = Game("Not a copy of Minecraft", 600, 480)
    main.run()
}

/*
* NEAR FUTURE:
* Fix transparency, make renderer from transparent stuff
* sprite renderer
* World generation (Biomes n' shit)
* Player class
* Hit detection
* Inventory
* GUI (Hotbar)
* Settings
* Simple lighting
* Day and night
* Skybox
*
* FUTURE:
* TextureAtlas class that manages the texture pack, allows you to rescue a texture at certain row and column ✓
* Smarter block class for building block and its texture from json file (https://github.com/Hopson97/MineCraft-One-Week-Challenge/tree/master/Res/Blocks) ✓
* Chunk system (Almost done)
* Entity (For future multiplayer support), skins
* Very simple world gen
* Multiplayer
* Obstacle hit detection
* Break and place blocks
* Sprite support for flowers and stuff
* Transparency, water...
* Make world generation smarter, ores.
* UI, hotbar, gui
* Settings
* world saving
* mobs
* day and night
* better, cooler, physically based lighting (https://learnopengl.com/PBR/Theory)
* Chat
* Voice chat
* Launcher
* Authentication - multiplayer authentication
* */

class Game(private val title: String, private val width: Int, private val height: Int) : Runnable {
    private lateinit var mouseInput: MouseInput
    private lateinit var window: Window
    private lateinit var frameHandler: FrameHandler
    private val timer: Timer = Timer()
    private val targetUps = 30 // target updates per second


    override fun run() {
        println("Initializing game! | LWJGL ${Version.getVersion()}")

        try {
            init()
            loop()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            cleanup()
        }
    }

    private fun init() {
        window = Window(width, height, title)
        mouseInput = MouseInput(window)
        window.open()
        frameHandler = FrameHandler(window, mouseInput)
    }

    private fun loop() {
         // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE) // WIREFRAME MODE

        var elapsedTime: Float
        var accumulator = 0f
        val interval = 1f / targetUps

        while (!window.shouldClose()) {
            elapsedTime = timer.getElapsedTime()
            accumulator += elapsedTime

            handleInput()

            while (accumulator >= interval) {
                handleUpdate(interval)
                accumulator -= interval
            }

            render()
        }

    }

    private fun handleInput() {
        mouseInput.input()
        frameHandler.input()

    }

    private fun handleUpdate(interval: Float) {
        frameHandler.update(interval)
    }

    private fun render() {
        frameHandler.render()
        window.update()
    }

    private fun cleanup() {
        window.shutdown()

        glfwTerminate()
        glfwSetErrorCallback(null)?.free()

        frameHandler.cleanup()
    }
}