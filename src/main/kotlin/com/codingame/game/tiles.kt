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
        val image: String
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

val tiles: Array<Tile> = arrayOf(
        Tile(0, parseShape("OO.|.OO"), 3, 7, 6, "tile0.png"),
        Tile(1, parseShape("OO.|.OO|OO."), 2, 3, 6, "tile1.png"),
        Tile(2, parseShape("OO.|.OO|..O"), 3, 10, 4, "tile2.png"),
        Tile(3, parseShape("OOOO|O..."), 2, 10, 3, "tile3.png"),
        Tile(4, parseShape("O...|OOOO|O..."), 2, 7, 2, "tile4.png"),
        Tile(5, parseShape("OO.|.OO|.OO"), 3, 8, 6, "tile5.png"),
        Tile(6, parseShape("..O..|OOOOO|..O.."), 1, 1, 4, "tile6.png"),
        Tile(7, parseShape("OO.|.OO"), 1, 3, 2, "tile7.png"),
        Tile(8, parseShape(".O..|OOOO|.O.."), 1, 0, 3, "tile8.png"),
        Tile(9, parseShape("OOOO"), 1, 3, 3, "tile9.png"),
        Tile(10, parseShape("O...|OOOO|...O"), 0, 1, 2, "tile10.png"),
        Tile(11, parseShape("OOO|OO."), 0, 2, 2, "tile11.png"),
        Tile(12, parseShape("OOO|.O.|.O."), 2, 5, 5, "tile12.png"),
        Tile(13, parseShape("OOOO|.O.."), 1, 3, 4, "tile13.png"),
        Tile(14, parseShape("OOO|O.O"), 0, 1, 2, "tile14.png"),
        Tile(15, parseShape("OOOO|OO.."), 3, 10, 5, "tile15.png"),
        Tile(16, parseShape("OOO.|..OO"), 1, 2, 3, "tile16.png"),
        Tile(17, parseShape("OOO|.O."), 0, 2, 2, "tile17.png"),
        Tile(18, parseShape("OOO"), 0, 2, 2, "tile18.png"),
        Tile(19, parseShape("OOO|O.."), 1, 4, 2, "tile19.png"),
        Tile(20, parseShape("O.O|OOO|O.O"), 0, 2, 3, "tile20.png"),
        Tile(21, parseShape(".OO.|OOOO|.OO."), 1, 5, 3, "tile21.png"),
        Tile(22, parseShape("OOO.|.OOO"), 0, 4, 2, "tile22.png"),
        Tile(23, parseShape(".O..|OOOO|..O."), 0, 2, 1, "tile23.png"),
        Tile(24, parseShape("OOOOO"), 1, 7, 1, "tile24.png"),
        Tile(25, parseShape("OOOO|O..O"), 1, 1, 5, "tile25.png"),
        Tile(26, parseShape("OO|OO"), 2, 6, 5, "tile26.png"),
        Tile(27, parseShape(".O.|OOO|.O."), 2, 5, 4, "tile27.png"),
        Tile(28, parseShape(".OO.|OOOO"), 2, 7, 4, "tile28.png"),
        Tile(29, parseShape("..O|OOO"), 2, 4, 6, "tile29.png"),
        Tile(30, parseShape("OO|.O"), 0, 1, 3, "tile30.png"),
        Tile(31, parseShape("O.|OO"), 0, 3, 1, "tile31.png"),
        Tile(32, parseShape("OO"), 0, 2, 1, "tile32.png")
)

val bonusTiles = arrayOf(
        Tile(-2, parseShape("O"), 0, 0, 0, "tile-1.png"),
        Tile(-3, parseShape("O"), 0, 0, 0, "tile-2.png"),
        Tile(-4, parseShape("O"), 0, 0, 0, "tile-3.png"),
        Tile(-5, parseShape("O"), 0, 0, 0, "tile-4.png"),
        Tile(-6, parseShape("O"), 0, 0, 0, "tile-5.png")
)
