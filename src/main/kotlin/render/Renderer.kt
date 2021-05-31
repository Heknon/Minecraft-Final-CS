package render

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*
import render.mesh.Mesh
import render.mesh.WorldObject
import render.texture.Texture
import render.texture.TextureProvider
import shader.FragmentShader
import shader.ShaderProgram
import shader.VertexShader
import utility.loadResource
import window.Window
import world.World

class Renderer(private val camera: Camera, window: Window, val textureProvider: TextureProvider) {
    private val worldShaderProgram = ShaderProgram()
    private val hudShaderProgram = ShaderProgram()
    val transformation: Transformer = Transformer(window, camera)

    val crosshairTexture = Texture("/textures/cross.png")
    val crosshairMesh = Mesh(
        listOf(
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
        ),
        listOf(
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f
        ),
        listOf(
            0, 1, 2, 2, 3, 0
        ),
        crosshairTexture
    )

    init {
        worldShaderProgram.registerShader(VertexShader("/world_vertex.glsl".loadResource(), worldShaderProgram.programId))
        worldShaderProgram.registerShader(FragmentShader("/world_fragment.glsl".loadResource(), worldShaderProgram.programId))
        worldShaderProgram.link()
        worldShaderProgram.createUniform("projectionMatrix")
        worldShaderProgram.createUniform("modelView")
        worldShaderProgram.createUniform("texture_sampler")

        hudShaderProgram.registerShader(VertexShader("/hud_vertex.glsl".loadResource(), hudShaderProgram.programId))
        hudShaderProgram.registerShader(FragmentShader("/hud_fragment.glsl".loadResource(), hudShaderProgram.programId))
        hudShaderProgram.link()
        hudShaderProgram.createUniform("crosshair_texture_sampler")
        hudShaderProgram.createUniform("aspect")
        hudShaderProgram.createUniform("scale")

    }

    fun render(window: Window, worldObjects: Collection<WorldObject>) {
        clear()

        if (window.shouldResize) {
            glViewport(0, 0, window.width, window.height)
            val aspect = window.width / window.height.toFloat()
            hudShaderProgram.setFloat("aspect", aspect)
            window.shouldResize = false
        }

        textureProvider.activateAtlas()

        // Bind shader program
        worldShaderProgram.bind()

        // Update projection matrix
        worldShaderProgram.setMatrix4f("projectionMatrix", transformation.getProjectionMatrix())

        camera.updateViewMatrix()

        // Set texture sampler
        worldShaderProgram.setInt("texture_sampler", 0)

        for (worldObject in worldObjects) {
            worldShaderProgram.setMatrix4f(
                "modelView", transformation.getModelViewMatrix(worldObject)
            )

            worldObject.mesh.render()
        }


        hudShaderProgram.bind()
        hudShaderProgram.setInt("crosshair_texture_sampler", 0)
        val aspect = window.width / window.height.toFloat()
        hudShaderProgram.setFloat("aspect", aspect)
        hudShaderProgram.setFloat("scale", 0.1f)
        crosshairMesh.render(true)


        hudShaderProgram.unbind()
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun cleanup() {
        worldShaderProgram.cleanup()
    }
}