package mesh

import org.joml.Vector3f

class Entity(
    position: Vector3f = Vector3f(0f, 0f, 0f),
    rotation: Vector3f = Vector3f(0f, 0f, 0f),
    scale: Float = 1f,
    mesh: Mesh
) : WorldObject(position, rotation, scale, mesh) {
}