package player

import math.RayCaster
import org.joml.Math.round
import org.joml.Math.toRadians
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import render.Camera
import render.Renderer
import render.Transformer
import render.mesh.Mesh
import utility.toLocation
import window.Window
import world.World
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class Player(
    override val position: Vector3f,
    override val rotation: Vector3f,
    private var world: World,
    override var mesh: Mesh,
    window: Window
) : Entity {
    override val velocity: Vector3f = Vector3f()
    override val scale: Float = 1f

    internal val camera = Camera(position, rotation)
    private val rayCaster = RayCaster(camera, window, world)

    private var acceleration = Vector3f()

    private val rotationBound = 89.9f

    private val reach = 6
    private val mouseSensitivity = 0.15f
    private val speed = 0.2f
    private val sprintSpeedMultiplier = 5

    val lookingAt get() = rayCaster.march(0.05f, reach) {
        val block = world.getBlockAt(it.x, it.y, it.z)
        return@march block != null && block.data.id != 0
    }?.toLocation(world)?.getBlock()

    val lookingAtNeighbor get() = rayCaster.peekMarch(0.01f, reach) { curr, next ->
        val currB = world.getBlockAt(curr.x, curr.y, curr.z)
        val nextB = world.getBlockAt(next.x, next.y, next.z)
        return@peekMarch nextB != null && nextB.data.id != 0 && nextB != currB
    }.toLocation(world).getBlock()

    fun handleInput(window: Window) {
        handleKeyboard(window)
        handleMouseInput(window)

        camera.position.set(position)
        camera.rotation.set(rotation)
    }

    fun update(interval: Float) {
        rayCaster.update()

        velocity.add(acceleration)
        acceleration.set(0f, 0f, 0f)

        position.x += velocity.x * interval
        position.y += velocity.y * interval
        position.z += velocity.z * interval

        velocity.x *= 0.95f
        velocity.z *= 0.95f
        velocity.y *= 0.95f
    }

    fun render(renderer: Renderer) {

    }

    fun handleKeyboard(window: Window) {
        if (window.isKeyPressed(GLFW_KEY_W)) {
            var s = speed
            if (window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) s *= sprintSpeedMultiplier
            acceleration.x += -cos(toRadians(rotation.y + 90)) * s
            acceleration.z += -sin(toRadians(rotation.y + 90)) * s
        }

        if (window.isKeyPressed(GLFW_KEY_S)) {
            acceleration.x += cos(toRadians(rotation.y + 90)) * speed
            acceleration.z += sin(toRadians(rotation.y + 90)) * speed
        }

        if (window.isKeyPressed(GLFW_KEY_A)) {
            acceleration.x += -cos(toRadians(rotation.y)) * speed
            acceleration.z += -sin(toRadians(rotation.y)) * speed
        }

        if (window.isKeyPressed(GLFW_KEY_D)) {
            acceleration.x += cos(toRadians(rotation.y)) * speed
            acceleration.z += sin(toRadians(rotation.y)) * speed
        }

        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            acceleration.y += speed * 3;
        }

        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            acceleration.y -= speed * 3;
        }
    }

    fun handleMouseInput(window: Window) {
        rotation.x += window.cursor.displayVector.x * mouseSensitivity
        rotation.y += window.cursor.displayVector.y * mouseSensitivity


        if (rotation.x > rotationBound) {
            rotation.x = rotationBound
        } else if (rotation.x < -rotationBound) {
            rotation.x = -rotationBound
        }

        if (rotation.y > 360) {
            rotation.y = 0f
        } else if (rotation.y < 0) {
            rotation.y = 360f
        }

        glfwSetCursorPos(window.id, window.width / 2.0, window.height / 2.0)
        window.cursor.updatePosition()
    }
}