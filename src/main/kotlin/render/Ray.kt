package render

import org.joml.Vector3f
import render.mesh.*
import render.texture.TextureProvider

class Ray(
    override val position: Vector3f,
    val direction: Vector3f,
    val extend: Float,
    override val rotation: Vector3f
) : WorldObject {

    override val scale: Float = 1f
    private val end = Vector3f(position).add(Vector3f(direction).mul(extend))
    override var mesh: Mesh = TexturedMesh(
        mutableListOf(
            position.x, position.y, position.z,
            end.x, end.y, end.z,
            end.x + 0.4f, end.y + 0.4f, end.z + 0.4f,
        ),
        mutableListOf(
            0f, 1f,
            1f, 0f
        ),
        null
    )

    init {
        println("=====-=-=-=-=-=-=-=-=-==")
        println("${direction.x}, ${direction.y}, ${direction.z}")
        println("${end.x}, ${end.y}, ${end.z}")
        println(mesh.positions)
        println("=====-=-=-=-=-=-=-=-=-==")
        println("")

        Renderer.rays.add(this)

    }
}