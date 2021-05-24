import org.joml.Vector3f
import render.Camera
import render.Renderer
import render.texture.TextureProvider
import utility.MousePicker
import window.MouseInput
import window.Window
import world.World
import world.chunk.block.Block
import kotlin.math.floor
import kotlin.math.sqrt

class FrameHandler(private val window: Window, private val mouseInput: MouseInput) {
    private val camera = Camera()
    private val renderer = Renderer(camera, window)
    private val textureProvider = TextureProvider("/textures/blocks.png", 16)
    private val world = World(textureProvider)
    private val mousePicker = MousePicker(window, camera, renderer.transformation)


    init {
        textureProvider.activateAtlas()
        world.generateWorld()
    }

    fun input() {
        camera.handleInput(window)
    }

    var ran = false
    fun update(interval: Float) {
        camera.updatePositioning(interval, mouseInput)


        //glfwSetCursorPos(window.windowId, (window.width / 2).toDouble(), (window.height / 2).toDouble())
        mousePicker.update()
        if (mouseInput.isLeftButtonPressed() && !ran) {
            //ran = true
            val selectedBlock = getLookingAt(6)
            world.getBlockDataById(0)?.let { selectedBlock?.updateData(it) }
        }

    }

    fun getLookingAt(radius: Int): Block? {
        return getLookingAt(camera.positionVector, camera.rotationVector, radius) { x: Long, y: Long, z: Long ->
            val blc = world.getBlockAt(x, y, z)
            return@getLookingAt blc != null && blc.data.id != 0
        }
    }


    fun getLookingAt(
        origin: Vector3f,
        direction: Vector3f,
        radius_: Int,
        callback: (Long, Long, Long) -> Boolean
    ): Block? {
        var x = floor(origin.x)
        var y = floor(origin.y)
        var z = floor(origin.z)

        val dx = direction.y
        val dy = -direction.x
        val dz = direction.z

        val stepX = signum(dx)
        val stepY = signum(dy)
        val stepZ = signum(dz)

        var tMaxX = intBound(origin.x, dx)
        var tMaxY = intBound(origin.y, dy)
        var tMaxZ = intBound(origin.z, dz)

        val tDeltaX = stepX / dx
        val tDeltaY = stepY / dy
        val tDeltaZ = stepZ / dz

        if (dx == 0f && dy == 0f && dz == 0f) {
            return null
        }

        val radius = radius_ / sqrt(dx * dx + dy * dy + dz * dz)

        while (true) {
            if (callback(x.toLong(), y.toLong(), z.toLong())) {
                break
            }

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    if (tMaxX > radius) break

                    x += stepX
                    tMaxX += tDeltaX
                } else {
                    if (tMaxZ > radius) break

                    z += stepZ
                    tMaxZ += tDeltaZ
                }
            } else {
                if (tMaxY < tMaxZ) {
                    if (tMaxY > radius) break

                    y += stepY
                    tMaxY += tDeltaY
                } else {
                    if (tMaxZ > radius) break

                    z += stepZ
                    tMaxZ += tDeltaZ
                }
            }
        }

        return world.getBlockAt(x, y, z)
    }

    fun signum(x: Float): Int {
        return if (x > 0) 1 else if (x < 0) -1 else 0
    }

    fun intBound(s: Float, ds: Float): Float {
        return if (ds < 0) {
            intBound(-s, -ds);
        } else {
            val q = mod(s, 1f);
            // problem is now s+t*ds = 1
            (1 - q) / ds;
        }
    }

    fun mod(value: Float, modulus: Float): Long {
        return (value.toLong() % modulus.toLong() + modulus).toLong() % modulus.toLong()
    }

    fun render() {
        renderer.render(window, world.chunks)
    }

    fun cleanup() {
        renderer.cleanup()
        world.cleanup()
    }
}