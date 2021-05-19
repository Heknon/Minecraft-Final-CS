import mesh.WorldObject
import org.lwjgl.opengl.GL11.*
import shader.FragmentShader
import shader.ShaderProgram
import shader.VertexShader

class Renderer(private val transformation: Transformation, private val camera: Camera) {
    private val shaderProgram = ShaderProgram()

    init {
        println("/fragment.glsl".loadResource())
        shaderProgram.registerShader(VertexShader("/vertex.glsl".loadResource(), shaderProgram.programId))
        shaderProgram.registerShader(FragmentShader("/fragment.glsl".loadResource(), shaderProgram.programId))
        shaderProgram.link()
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createUniform("modelView")
        shaderProgram.createUniform("texture_sampler")
    }

    fun render(window: Window, worldObjects: List<WorldObject>) {
        clear()

        if (window.shouldResize) {
            glViewport(0, 0, window.width, window.height)
            window.shouldResize = false
        }

        // Bind shader program
        shaderProgram.bind()

        // Update projection matrix
        shaderProgram.setMatrix4f("projectionMatrix", transformation.getProjectionMatrix())

        camera.updateViewMatrix()

        // Set texture sampler
        shaderProgram.setInt("texture_sampler", 0)

        for (worldObject in worldObjects) {
            shaderProgram.setMatrix4f(
                "modelView", transformation.getModelViewMatrix(worldObject)
            )

            worldObject.mesh.render()
        }

        shaderProgram.unbind()
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}