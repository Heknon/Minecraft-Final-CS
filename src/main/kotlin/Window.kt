import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL

class Window(width: Int, height: Int, title: String) {
    var width: Int = width
        private set

    var height: Int = height
        private set

    var title: String = title
        set(value) {
            // TODO: Change window title and set accordingly
            field = value
            throw NotImplementedError("// TODO: Change window title and set accordingly")
        }

    var shouldResize = false
    val windowId: Long

    init {
        GLFWErrorCallback.createPrint(System.err).set() // set default error callback to print to System.err stream

        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW!")
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        val window = glfwCreateWindow(width, height, title, NULL, NULL)
        windowId = window

        if (window == NULL) {
            throw RuntimeException("Couldn't create GLFW window")
        }

        glfwSetKeyCallback(window) { win, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true)
            }
        }

        glfwSetFramebufferSizeCallback(window) { win, w, h ->
            this.width = w
            this.height = h
            shouldResize = true
        }

        try {
            val stack = stackPush()
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)

            glfwGetWindowSize(window, pWidth, pHeight)

            val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!

            glfwSetWindowPos(
                window,
                (videoMode.width() - pWidth.get(0)) / 2, (videoMode.height() - pHeight.get(0)) / 2
            )
        } catch (e: Exception) {

        }

        glfwMakeContextCurrent(window)
        glfwSwapInterval(1) // enable v-sync

        GL.createCapabilities()
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_TEXTURE_2D)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
    }

    fun open() {
        glfwShowWindow(windowId)
    }

    fun isKeyPressed(keyCode: Int): Boolean {
        return glfwGetKey(windowId, keyCode) == GLFW_PRESS
    }

    fun shouldClose(): Boolean {
        return glfwWindowShouldClose(windowId)
    }

    fun update() {
        glfwSwapBuffers(windowId)

        glfwPollEvents() // poll for window events
    }

    fun shutdown() {
        Callbacks.glfwFreeCallbacks(windowId)
        glfwDestroyWindow(windowId)
    }
}