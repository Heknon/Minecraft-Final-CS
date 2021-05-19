import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*


class MouseInput(private val window: Window) {
    private var previousPos: Vector2d? = null

    private var currentPos: Vector2d? = null

    var displVec: Vector2f? = null

    private var inWindow = false

    private var leftButtonPressed = false

    private var rightButtonPressed = false

    init {
        previousPos = Vector2d(-1.0, -1.0)
        currentPos = Vector2d(0.0, 0.0)
        displVec = Vector2f()
    }

    init {
        glfwSetCursorPosCallback(window.windowId) { windowHandle: Long, xpos: Double, ypos: Double ->
            currentPos!!.x = xpos
            currentPos!!.y = ypos
        }
        glfwSetCursorEnterCallback(
            window.windowId
        ) { windowHandle: Long, entered: Boolean -> inWindow = entered }
        glfwSetMouseButtonCallback(window.windowId) { windowHandle: Long, button: Int, action: Int, mode: Int ->
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS
        }
    }

    fun input() {
        displVec!!.x = 0f
        displVec!!.y = 0f
        if (previousPos!!.x > 0 && previousPos!!.y > 0 && inWindow) {
            val deltax = currentPos!!.x - previousPos!!.x
            val deltay = currentPos!!.y - previousPos!!.y
            val rotateX = deltax != 0.0
            val rotateY = deltay != 0.0
            if (rotateX) {
                displVec!!.y = deltax.toFloat()
            }
            if (rotateY) {
                displVec!!.x = deltay.toFloat()
            }
        }
        previousPos!!.x = currentPos!!.x
        previousPos!!.y = currentPos!!.y
    }

    fun isLeftButtonPressed(): Boolean {
        return leftButtonPressed
    }

    fun isRightButtonPressed(): Boolean {
        return rightButtonPressed
    }
}