package com.codingame.game

typealias PatchShape = List<List<Boolean>>
val PatchShape.width: Int get() = get(0).size
val PatchShape.height: Int get() = size

data class Patch(
        val id: Int,
        val shape: PatchShape,
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

private fun parseShape(s: String) : PatchShape =
        s.split("|").map { it.map { it == 'O' } }

fun PatchShape.string() =
        this.joinToString("|") { it.joinToString("") { if (it) "O" else "."} }

val PatchShape.rightRotated: PatchShape get() =
        List(width) { row -> this.map { it[row] }.reversed() }

val PatchShape.all : List<PatchShape> get() {
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

val finalPatch = Patch(32, parseShape("OO"), 0, 2, 1, "patch32.png", listOf(0 to 0, 120 to 0, 120 to 60, 0 to 60))

val patches = listOf(
        Patch(0, parseShape("OO.|.OO"), 3, 7, 6, "patch0.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Patch(1, parseShape("OO.|.OO|OO."), 2, 3, 6, "patch1.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 120 to 120, 120 to 180, 0 to 180, 0 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Patch(2, parseShape("OO.|.OO|..O"), 3, 10, 4, "patch2.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 180, 120 to 180, 120 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Patch(3, parseShape("OOOO|O..."), 2, 10, 3, "patch3.png", listOf(0 to 0, 240 to 0, 240 to 60, 60 to 60, 60 to 120, 0 to 120)),
        Patch(4, parseShape("O...|OOOO|O..."), 2, 7, 2, "patch4.png", listOf(0 to 0, 60 to 0, 60 to 60, 240 to 60, 240 to 120, 60 to 120, 60 to 180, 0 to 180)),
        Patch(5, parseShape("OO.|.OO|.OO"), 3, 8, 6, "patch5.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 180, 60 to 180, 60 to 60, 0 to 60)),
        Patch(6, parseShape("..O..|OOOOO|..O.."), 1, 1, 4, "patch6.png", listOf(120 to 0, 180 to 0, 180 to 60, 300 to 60, 300 to 120, 180 to 120, 180 to 180, 120 to 180, 120 to 120, 0 to 120, 0 to 60, 120 to 60)),
        Patch(7, parseShape("OO.|.OO"), 1, 3, 2, "patch7.png", listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 60 to 120, 60 to 60,0 to 60)),
        Patch(8, parseShape(".O..|OOOO|.O.."), 1, 0, 3, "patch8.png", listOf(60 to 0, 120 to 0, 120 to 60, 240 to 60, 240 to 120, 120 to 120, 120 to 180, 60 to 180, 60 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Patch(9, parseShape("OOOO"), 1, 3, 3, "patch9.png", listOf(0 to 0, 240 to 0, 240 to 60, 0 to 60)),
        Patch(10, parseShape("O...|OOOO|...O"), 0, 1, 2, "patch10.png", listOf(0 to 0, 60 to 0, 60 to 60, 240 to 60, 240 to 180, 180 to 180, 180 to 120, 0 to 120)),
        Patch(11, parseShape("OOO|OO."), 0, 2, 2, "patch11.png", listOf(0 to 0, 180 to 0, 180 to 60, 120 to 60, 120 to 120, 0 to 120)),
        Patch(12, parseShape("OOO|.O.|.O."), 2, 5, 5, "patch12.png", listOf(0 to 0, 180 to 0, 180 to 60, 120 to 60, 120 to 180, 60 to 180, 60 to 60, 0 to 60)),
        Patch(13, parseShape("OOOO|.O.."), 1, 3, 4, "patch13.png", listOf(0 to 0, 240 to 0, 240 to 60, 120 to 60, 120 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Patch(14, parseShape("OOO|O.O"), 0, 1, 2, "patch14.png", listOf(0 to 0, 180 to 0, 180 to 120, 120 to 120, 120 to 60, 60 to 60, 60 to 120, 0 to 120)),
        Patch(15, parseShape("OOOO|OO.."), 3, 10, 5, "patch15.png", listOf(0 to 0, 240 to 0, 240 to 60, 120 to 60, 120 to 120, 0 to 120)),
        Patch(16, parseShape("OOO.|..OO"), 1, 2, 3, "patch16.png", listOf(0 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 120 to 120, 120 to 60, 0 to 60)),
        Patch(17, parseShape("OOO|.O."), 0, 2, 2, "patch17.png", listOf(0 to 0, 180 to 0, 180 to 60, 120 to 60, 120 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Patch(18, parseShape("OOO"), 0, 2, 2, "patch18.png", listOf(0 to 0, 180 to 0, 180 to 60, 0 to 60)),
        Patch(19, parseShape("OOO|O.."), 1, 4, 2, "patch19.png", listOf(0 to 0, 180 to 0, 180 to 60, 60 to 60, 60 to 120, 0 to 120)),
        Patch(20, parseShape("O.O|OOO|O.O"), 0, 2, 3, "patch20.png", listOf(0 to 0, 60 to 0, 60 to 60, 120 to 60, 120 to 0, 180 to 0, 180 to 180, 120 to 180, 120 to 120, 60 to 120, 60 to 180, 0 to 180)),
        Patch(21, parseShape(".OO.|OOOO|.OO."), 1, 5, 3, "patch21.png", listOf(60 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 180 to 120, 180 to 180, 60 to 180, 60 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Patch(22, parseShape("OOO.|.OOO"), 0, 4, 2, "patch22.png", listOf(0 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Patch(23, parseShape(".O..|OOOO|..O."), 0, 2, 1, "patch23.png", listOf(60 to 0, 120 to 0, 120 to 60, 240 to 60, 240 to 120, 180 to 120, 180 to 180, 120 to 180, 120 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Patch(24, parseShape("OOOOO"), 1, 7, 1, "patch24.png", listOf(0 to 0, 300 to 0, 300 to 60, 0 to 60)),
        Patch(25, parseShape("OOOO|O..O"), 1, 1, 5, "patch25.png", listOf(0 to 0, 240 to 0, 240 to 120, 180 to 120, 180 to 60, 60 to 60, 60 to 120, 0 to 120)),
        Patch(26, parseShape("OO|OO"), 2, 6, 5, "patch26.png", listOf(0 to 0, 120 to 0, 120 to 120, 0 to 120)),
        Patch(27, parseShape(".O.|OOO|.O."), 2, 5, 4, "patch27.png", listOf(60 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 120 to 120, 120 to 180, 60 to 180, 60 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Patch(28, parseShape(".OO.|OOOO"), 2, 7, 4, "patch28.png", listOf(60 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 0 to 120, 0 to 60, 60 to 60)),
        Patch(29, parseShape("..O|OOO"), 2, 4, 6, "patch29.png", listOf(120 to 0, 180 to 0, 180 to 120, 0 to 120, 0 to 60, 120 to 60)),
        Patch(30, parseShape("OO|.O"), 0, 1, 3, "patch30.png", listOf(0 to 0, 120 to 0, 120 to 120, 60 to 120, 60 to 60, 0 to 60)),
        Patch(31, parseShape("O.|OO"), 0, 3, 1, "patch31.png", listOf(0 to 0, 60 to 0, 60 to 60, 120 to 60, 120 to 120, 0 to 120))
)

val bonusPatches by lazy {
        league.patchTurns.mapIndexed { index, i ->
                Patch(-i, parseShape("O"), 0, 0, 0, "patch-${index + 1}.png")
        }
}