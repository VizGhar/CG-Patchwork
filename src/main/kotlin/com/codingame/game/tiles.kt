package com.codingame.game

typealias TileShape = List<List<Boolean>>
val TileShape.width: Int get() = get(0).size
val TileShape.height: Int get() = size

data class Tile(
        val id: Int,
        val shape: TileShape,
        val earn: Int,
        val price: Int,
        val time: Int,
        val image: String,
        val polygon: List<Pair<Int, Int>> = emptyList()
) {
        override fun toString(): String {
                return "$id $earn $price $time ${shape.string()}"
        }
}

private fun parseShape(s: String) : TileShape =
        s.split("|").map { it.map { it == 'O' } }

fun TileShape.string() =
        this.joinToString("|") { it.joinToString("") { if (it) "O" else "."} }

val TileShape.mirrored: TileShape get() =
        this.map { it.reversed() }

val TileShape.rightRotated: TileShape get() =
        List(width) { row -> this.map { it[row] }.reversed() }

val TileShape.all : List<TileShape> get() {
        val mirror = this.map { it.reversed() }
        return listOf(
                this,
                this.rightRotated,
                this.rightRotated.rightRotated,
                this.rightRotated.rightRotated.rightRotated,
                mirror,
                mirror.rightRotated,
                mirror.rightRotated.rightRotated,
                mirror.rightRotated.rightRotated.rightRotated,
        )
}

val finalTile = Tile(32, parseShape("OO"), 0, 2, 1, "tile32.png", listOf(0 to 0, 120 to 0, 120 to 60, 0 to 60))

val tiles = listOf(
        Tile(0, parseShape("OO.|.OO"), 3, 7, 6, "tile0.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Tile(1, parseShape("OO.|.OO|OO."), 2, 3, 6, "tile1.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 120 to 120, 120 to 180, 0 to 180, 0 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Tile(2, parseShape("OO.|.OO|..O"), 3, 10, 4, "tile2.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 180, 120 to 180, 120 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Tile(3, parseShape("OOOO|O..."), 2, 10, 3, "tile3.png", listOf(0 to 0, 240 to 0, 240 to 60, 60 to 60, 60 to 120, 0 to 120)),
        Tile(4, parseShape("O...|OOOO|O..."), 2, 7, 2, "tile4.png", listOf(0 to 0, 60 to 0, 60 to 60, 240 to 60, 240 to 120, 60 to 120, 60 to 180, 0 to 180)),
        Tile(5, parseShape("OO.|.OO|.OO"), 3, 8, 6, "tile5.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 180, 60 to 180, 60 to 60, 0 to 60)),
        Tile(6, parseShape("..O..|OOOOO|..O.."), 1, 1, 4, "tile6.png", listOf(120 to 0, 180 to 0, 180 to 60, 300 to 60, 300 to 120, 180 to 120, 180 to 180, 120 to 180, 120 to 120, 0 to 120, 0 to 60, 120 to 60)),
        Tile(7, parseShape("OO.|.OO"), 1, 3, 2, "tile7.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 60 to 120, 60 to 60,0 to 60)),
        Tile(8, parseShape(".O..|OOOO|.O.."), 1, 0, 3, "tile8.png", listOf(60 to 0, 120 to 0, 120 to 60, 240 to 60, 240 to 120, 120 to 120, 120 to 180, 60 to 180, 60 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Tile(9, parseShape("OOOO"), 1, 3, 3, "tile9.png", listOf(0 to 0, 240 to 0, 240 to 60, 0 to 60)),
        Tile(10, parseShape("O...|OOOO|...O"), 0, 1, 2, "tile10.png", listOf(0 to 0, 60 to 0, 60 to 60, 240 to 60, 240 to 180, 180 to 180, 180 to 120, 0 to 120)),
        Tile(11, parseShape("OOO|OO."), 0, 2, 2, "tile11.png", listOf(0 to 0, 180 to 0, 180 to 60, 120 to 60, 120 to 120, 0 to 120)),
        Tile(12, parseShape("OOO|.O.|.O."), 2, 5, 5, "tile12.png", listOf(0 to 0, 180 to 0, 180 to 60, 120 to 60, 120 to 180, 60 to 180, 60 to 60, 0 to 60)),
        Tile(13, parseShape("OOOO|.O.."), 1, 3, 4, "tile13.png", listOf(0 to 0, 240 to 0, 240 to 60, 120 to 60, 120 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Tile(14, parseShape("OOO|O.O"), 0, 1, 2, "tile14.png", listOf(0 to 0, 180 to 0, 180 to 120, 120 to 120, 120 to 60, 60 to 60, 60 to 120, 0 to 120)),
        Tile(15, parseShape("OOOO|OO.."), 3, 10, 5, "tile15.png", listOf(0 to 0, 240 to 0, 240 to 60, 120 to 60, 120 to 120, 0 to 120)),
        Tile(16, parseShape("OOO.|..OO"), 1, 2, 3, "tile16.png", listOf(0 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 120 to 120, 120 to 60, 0 to 60)),
        Tile(17, parseShape("OOO|.O."), 0, 2, 2, "tile17.png", listOf(0 to 0, 180 to 0, 180 to 60, 120 to 60, 120 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Tile(18, parseShape("OOO"), 0, 2, 2, "tile18.png", listOf(0 to 0, 180 to 0, 180 to 60, 0 to 60)),
        Tile(19, parseShape("OOO|O.."), 1, 4, 2, "tile19.png", listOf(0 to 0, 180 to 0, 180 to 60, 60 to 60, 60 to 120, 0 to 120)),
        Tile(20, parseShape("O.O|OOO|O.O"), 0, 2, 3, "tile20.png", listOf(0 to 0, 60 to 0, 60 to 60, 120 to 60, 120 to 0, 180 to 0, 180 to 180, 120 to 180, 120 to 120, 60 to 120, 60 to 180, 0 to 180)),
        Tile(21, parseShape(".OO.|OOOO|.OO."), 1, 5, 3, "tile21.png", listOf(60 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 180 to 120, 180 to 180, 60 to 180, 60 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Tile(22, parseShape("OOO.|.OOO"), 0, 4, 2, "tile22.png", listOf(0 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Tile(23, parseShape(".O..|OOOO|..O."), 0, 2, 1, "tile23.png", listOf(60 to 0, 120 to 0, 120 to 60, 240 to 60, 240 to 120, 180 to 120, 180 to 180, 120 to 180, 120 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Tile(24, parseShape("OOOOO"), 1, 7, 1, "tile24.png", listOf(0 to 0, 300 to 0, 300 to 60, 0 to 60)),
        Tile(25, parseShape("OOOO|O..O"), 1, 1, 5, "tile25.png", listOf(0 to 0, 240 to 0, 240 to 120, 180 to 120, 180 to 60, 60 to 60, 60 to 120, 0 to 120)),
        Tile(26, parseShape("OO|OO"), 2, 6, 5, "tile26.png", listOf(0 to 0, 120 to 0, 120 to 120, 0 to 120)),
        Tile(27, parseShape(".O.|OOO|.O."), 2, 5, 4, "tile27.png", listOf(60 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 120 to 120, 120 to 180, 60 to 180, 60 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Tile(28, parseShape(".OO.|OOOO"), 2, 7, 4, "tile28.png", listOf(60 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Tile(29, parseShape("..O|OOO"), 2, 4, 6, "tile29.png", listOf(120 to 0, 180 to 0, 180 to 120, 0 to 120, 0 to 60, 120 to 60)),
        Tile(30, parseShape("OO|.O"), 0, 1, 3, "tile30.png", listOf(0 to 0, 120 to 0, 120 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Tile(31, parseShape("O.|OO"), 0, 3, 1, "tile31.png", listOf(0 to 0, 60 to 0, 60 to 60, 120 to 60, 120 to 120, 0 to 120))
)

val bonusTiles by lazy {
        league.patchTurns.mapIndexed { index, i ->
                Tile(-i, parseShape("O"), 0, 0, 0, "tile-${index + 1}.png")
        }
}