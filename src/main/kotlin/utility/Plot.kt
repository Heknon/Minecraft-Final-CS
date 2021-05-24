package utility

import org.joml.Vector3f
import org.joml.Vector3i
import kotlin.math.floor

interface Plot<T> {
    fun next(): Boolean
    fun reset()
    fun end()
    fun get(): T
}

class PlotCell3f(offX: Float, offY: Float, offZ: Float, width: Float, height: Float, depth: Float) : Plot<Vector3f> {
    val size = Vector3f()
    val off = Vector3f()
    val pos = Vector3f()
    val dir = Vector3f()

    private val index = Vector3f()

    val delta = Vector3f()
    val sign = Vector3f()
    val max = Vector3f()

    private var limit: Int = 0
    private var plotted: Int = 0

    init {
        off.set(offX, offY, offZ)
        size.set(width, height, depth)
    }

    fun plot(position: Vector3f, direction: Vector3f, cells: Int) {
        limit = cells

        pos.set(position)
        dir.normalize(direction)

        delta.set(size)
        delta.div(dir)

        sign.x = if (dir.x > 0) 1f else if (dir.x < 0) -1f else 0f
        sign.y = if (dir.y > 0) 1f else if (dir.y < 0) -1f else 0f
        sign.z = if (dir.z > 0) 1f else if (dir.z < 0) -1f else 0f

        reset()
    }


    override fun next(): Boolean {
        if (plotted++ > 0) {
            val mx = sign.x * max.x
            val my = sign.y * max.y
            val mz = sign.z * max.z

            if (mx < my && mx < mz) {
                max.x += delta.x
                index.x += sign.x
            } else if (mz < my && mz < mx) {
                max.z += delta.z
                index.z += sign.z
            } else {
                max.y += delta.y
                index.y += sign.y
            }
        }

        return plotted <= limit
    }

    override fun reset() {
        plotted = 0

        index.x = floor((pos.x - off.x) / size.x)
        index.y = floor((pos.y - off.y) / size.y)
        index.z = floor((pos.z - off.z) / size.z)

        val ax = index.x * size.x + off.x
        val ay = index.y * size.y + off.y
        val az = index.z * size.z + off.z

        max.x = if (sign.x > 0) ax + size.x - pos.x else pos.x - ax
        max.y = if (sign.y > 0) ay + size.y - pos.y else pos.y - ay
        max.z = if (sign.z > 0) az + size.z - pos.z else pos.z - az
        max.div(dir)
    }

    override fun end() {
        plotted = limit + 1
    }

    override fun get(): Vector3f {
        return index
    }

    fun actual(): Vector3f {
        return Vector3f(
            index.x * size.x + off.x,
            index.y * size.y + off.y,
            index.z * size.z + off.z
        )
    }

    fun offset(x: Float, y: Float, z: Float) {
        off.set(x, y, z)
    }
}