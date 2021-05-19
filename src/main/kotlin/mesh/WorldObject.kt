package mesh

import org.joml.Vector3f

open class WorldObject(
    val position: Vector3f = Vector3f(
        0f,
        0f,
        0f
    ),
    val rotation: Vector3f = Vector3f(0f, 0f, 0f),
    val scale: Float = 1f,
    val mesh: Mesh
) {
}