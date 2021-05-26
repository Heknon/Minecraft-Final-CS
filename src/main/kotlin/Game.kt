import org.lwjgl.Version
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwTerminate
import utility.Timer
import window.Cursor
import window.Window

fun main() {
    val main = Game("Not a copy of Minecraft", 600, 480)
    main.run()
}

/*
* NEAR FUTURE:
* ASAP: Fix mouse input
* Fix transparency, make renderer from transparent stuff
* Make block accessing generate chunk if block doesnt exist, same with chunk - Chunk system
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
* world saving (use file sent to eden on discord a year ago as reference)
* mobs
* day and night
* better, cooler, physically based lighting (https://learnopengl.com/PBR/Theory)
* Chat
* Voice chat
* Launcher
* Authentication - multiplayer authentication
*
* DONE:
* TextureAtlas class that manages the texture pack, allows you to rescue a texture at certain row and column ✓
* Smarter block class for building block and its texture from json file (https://github.com/Hopson97/MineCraft-One-Week-Challenge/tree/master/Res/Blocks) ✓
* */

class Game(private val title: String, private val width: Int, private val height: Int) : Runnable {
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
        window.open()
        frameHandler = FrameHandler(window)
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
        frameHandler.window.cursor.input(window)
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