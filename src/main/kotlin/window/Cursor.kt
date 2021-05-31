package window

import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*


class Cursor(private val window: Window) {
    private val previousPos: Vector2d = Vector2d(-1.0, -1.0)

    private val currentPos: Vector2d = Vector2d(0.0, 0.0)

    val previousPosClone get () = Vector2d(previousPos)

    val currentPosClone get() = Vector2d(currentPos)

    val displayVector = Vector2f()

    var inWindow = false

    private var leftButtonPressed = false

    private var rightButtonPressed = false

    init {
        glfwSetCursorPosCallback(window.id) { _: Long, xPos: Double, yPos: Double ->
            currentPos.x = xPos
            currentPos.y = yPos
        }

        glfwSetCursorEnterCallback(window.id) { _: Long, entered: Boolean ->
            inWindow = entered
        }

        glfwSetMouseButtonCallback(window.id) { _: Long, button: Int, action: Int, _: Int ->
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS
        }

        disableMouse()
        setRawMode()
    }

    fun input(window: Window?) {
        displayVector.x = 0f
        displayVector.y = 0f
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            val deltaX = currentPos.x - previousPos.x
            val deltaY = currentPos.y - previousPos.y
            val rotateX = deltaX != 0.0
            val rotateY = deltaY != 0.0
            if (rotateX) {
                displayVector.y = deltaX.toFloat()
            }
            if (rotateY) {
                displayVector.x = deltaY.toFloat()
            }
        }
        previousPos.x = currentPos.x
        previousPos.y = currentPos.y
    }


    fun disableMouse() {
        glfwSetInputMode(window.id, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
    }

    fun setRawMode() {
        if (glfwRawMouseMotionSupported()) {
            println("success")
            glfwSetInputMode(window.id, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE)
        }
    }

    fun enableMouse() {
        glfwSetInputMode(window.id, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
    }

    fun updatePosition() {
        val resetPos = Vector2d(window.width / 2.0, window.height / 2.0)
        currentPos.set(resetPos)
        previousPos.set(resetPos)
    }

    fun isLeftButtonPressed(): Boolean {
        return leftButtonPressed
    }

    fun isRightButtonPressed(): Boolean {
        return rightButtonPressed
    }
}