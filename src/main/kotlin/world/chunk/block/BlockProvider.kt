package world.chunk.block

import render.texture.TextureProvider
import utility.readResourceAsJson

class BlockProvider(private val textureProvider: TextureProvider) {
    // Create block meshes from json
    // hand over to block container to be handled by world

    private val blocksJson = "/blocks.json".readResourceAsJson()
    val blocks: MutableMap<Int, BlockData> = mutableMapOf()

    enum class Direction {
        Up,
        Down,
        West,
        East,
        North,
        South,
        All
    }

    data class BlockFaceMesh(
        val positions: FloatArray,
        val uvs: FloatArray,
        val indices: IntArray,
        val direction: Direction
    )

    private val sides = setOf(
        Direction.South,
        Direction.North,
        Direction.East,
        Direction.West,
    )


    private val positions = floatArrayOf(
        // TOP FACE
        -0.5f, 0.5f, -0.5f, // left top back
        -0.5f, 0.5f, 0.5f, // left top front
        0.5f, 0.5f, 0.5f, // right top front
        0.5f, 0.5f, -0.5f, // right top back

        // BOTTOM FACE
        -0.5f, -0.5f, -0.5f, // left bottom back
        -0.5f, -0.5f, 0.5f, // left bottom front
        0.5f, -0.5f, 0.5f, // right bottom front
        0.5f, -0.5f, -0.5f, // right bottom back

        // LEFT FACE
        -0.5f, 0.5f, 0.5f, // left top front
        -0.5f, -0.5f, 0.5f, // left bottom front
        -0.5f, -0.5f, -0.5f, // left bottom back
        -0.5f, 0.5f, -0.5f, // left top back

        // RIGHT FACE
        0.5f, 0.5f, 0.5f, // right top front
        0.5f, -0.5f, 0.5f, // right bottom front
        0.5f, -0.5f, -0.5f, // right bottom back
        0.5f, 0.5f, -0.5f, // right top back

        // FRONT FACE
        -0.5f, 0.5f, 0.5f, // left top front
        -0.5f, -0.5f, 0.5f, // left bottom front
        0.5f, -0.5f, 0.5f, // right bottom front
        0.5f, 0.5f, 0.5f, // right top front

        // BACK FACE
        -0.5f, 0.5f, -0.5f, // left top back
        -0.5f, -0.5f, -0.5f, // left bottom back
        0.5f, -0.5f, -0.5f, // right bottom back
        0.5f, 0.5f, -0.5f, // right top back
    )

    init {

        blocksJson.keys.forEach { k ->
            val faces = mutableMapOf<Direction, BlockFaceMesh>()
            val blockData = blocksJson[k] as Map<*, *>
            val textureData = blockData["texture"] as Map<String, List<Int>>

            for ((j, i) in faceOrder.withIndex()) {
                val pos = FloatArray(12) {
                    positions[j * 12 + it]
                }

                val textureCoords = when {
                    textureData.containsKey("all") ->
                        textureProvider.getTexture(textureData["all"]!![0], textureData["all"]!![1])
                    textureData.containsKey("sides") && sides.contains(i) ->
                        textureProvider.getTexture(textureData["sides"]!![0], textureData["sides"]!![1])
                    textureData.containsKey(i.name.toLowerCase()) ->
                        textureProvider.getTexture(
                            textureData[i.name.toLowerCase()]!![0],
                            textureData[i.name.toLowerCase()]!![1]
                        )
                    else -> textureProvider.emptyTexture()
                }

                val face = BlockFaceMesh(
                    pos,
                    textureCoords.getDefaultUVArray(),
                    intArrayOf(0, 1, 3, 3, 1, 2),
                    i
                )

                faces[i] = face
            }

            val block = BlockData(
                blockData["id"] as Int,
                blockData["name"] as String,
                faces,
                blockData["collidable"] as Boolean,
                blockData["transparent"] as Boolean? ?: false
            )

            blocks[block.id] = block
        }
    }

    companion object {
        val faceOrder = Direction.values().dropLast(1)
    }

}