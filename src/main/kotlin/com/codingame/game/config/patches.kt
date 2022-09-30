package com.codingame.game.config

typealias PatchShape = List<List<Boolean>>
val PatchShape.width: Int get() = get(0).size
val PatchShape.height: Int get() = size

data class Patch(
        val id: Int,
        val shape: PatchShape,
        val earn: Int,
        val price: Int,
        val time: Int
) {
        override fun toString(): String {
                return "$id $earn $price $time ${shape.string()}"
        }
}

private fun parseShape(s: String) : PatchShape =
        s.split("|").map { it.map { it == 'O' } }

fun PatchShape.string() =
        this.joinToString("|") { it.joinToString("") { if (it) "O" else "."} }

val PatchShape.rightRotated: PatchShape get() =
        List(width) { row -> this.map { it[row] }.reversed() }

val PatchShape.flipped: PatchShape get() =
        this.map { it.reversed() }

val PatchShape.all : List<PatchShape> get() {
        return listOf(
                this,
                rightRotated,
                rightRotated.rightRotated,
                rightRotated.rightRotated.rightRotated,
                flipped,
                flipped.rightRotated,
                flipped.rightRotated.rightRotated,
                flipped.rightRotated.rightRotated.rightRotated,
        )
}

/**
 * After shuffling [patches] this patch will be added at the end of a queue
 */
val finalPatch = Patch(32, parseShape("OO"), 0, 2, 1)

val patches = listOf(
        Patch(0, parseShape("OO.|.OO"), 3, 7, 6),
        Patch(1, parseShape("OO.|.OO|OO."), 2, 3, 6),
        Patch(2, parseShape("OO.|.OO|..O"), 3, 10, 4),
        Patch(3, parseShape("OOOO|O..."), 2, 10, 3),
        Patch(4, parseShape("O...|OOOO|O..."), 2, 7, 2),
        Patch(5, parseShape("OO.|.OO|.OO"), 3, 8, 6),
        Patch(6, parseShape("..O..|OOOOO|..O.."), 1, 1, 4),
        Patch(7, parseShape("OO.|.OO"), 1, 3, 2),
        Patch(8, parseShape(".O..|OOOO|.O.."), 1, 0, 3),
        Patch(9, parseShape("OOOO"), 1, 3, 3),
        Patch(10, parseShape("O...|OOOO|...O"), 0, 1, 2),
        Patch(11, parseShape("OOO|OO."), 0, 2, 2),
        Patch(12, parseShape("OOO|.O.|.O."), 2, 5, 5),
        Patch(13, parseShape("OOOO|.O.."), 1, 3, 4),
        Patch(14, parseShape("OOO|O.O"), 0, 1, 2),
        Patch(15, parseShape("OOOO|OO.."), 3, 10, 5),
        Patch(16, parseShape("OOO.|..OO"), 1, 2, 3),
        Patch(17, parseShape("OOO|.O."), 0, 2, 2),
        Patch(18, parseShape("OOO"), 0, 2, 2),
        Patch(19, parseShape("OOO|O.."), 1, 4, 2),
        Patch(20, parseShape("O.O|OOO|O.O"), 0, 2, 3),
        Patch(21, parseShape(".OO.|OOOO|.OO."), 1, 5, 3),
        Patch(22, parseShape("OOO.|.OOO"), 0, 4, 2),
        Patch(23, parseShape(".O..|OOOO|..O."), 0, 2, 1),
        Patch(24, parseShape("OOOOO"), 1, 7, 1),
        Patch(25, parseShape("OOOO|O..O"), 1, 1, 5),
        Patch(26, parseShape("OO|OO"), 2, 6, 5),
        Patch(27, parseShape(".O.|OOO|.O."), 2, 5, 4),
        Patch(28, parseShape(".OO.|OOOO"), 2, 7, 4),
        Patch(29, parseShape("..O|OOO"), 2, 4, 6),
        Patch(30, parseShape("OO|.O"), 0, 1, 3),
        Patch(31, parseShape("O.|OO"), 0, 3, 1),
)

val bonusPatches by lazy { league.patchTurns.map { i -> Patch(-i, parseShape("O"), 0, 0, 0) } }
