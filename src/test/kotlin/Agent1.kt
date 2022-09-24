import java.util.*

typealias PatchShape = List<List<Boolean>>

private const val BOARD_WIDTH = 9
private const val BOARD_HEIGHT = 9

data class Patch(
    val id: Int,
    val shape: PatchShape,
    val earn: Int,
    val price: Int,
    val time: Int
)

val PatchShape.width: Int get() = get(0).size

fun Scanner.nextPatch() : Patch {
    val patchId = nextInt()
    val patchEarn = nextInt()
    val patchPrice = nextInt()
    val patchTime = nextInt()
    val shape = next()
    nextLine()
    return Patch(patchId, parseShape(shape), patchEarn, patchPrice, patchTime)
}

private fun parseShape(s: String) : PatchShape =
    s.split("|").map { it.map { it == 'O' } }

val PatchShape.mirrored: PatchShape get() =
    this.map { it.reversed() }

val PatchShape.rightRotated: PatchShape get() =
    List(width) { row -> this.map { it[row] }.reversed() }

private fun tryApplyPatchToBoard(board: Array<Array<Boolean>>, patchShape: PatchShape, x: Int, y: Int) : Boolean {
    for (shapeY in patchShape.indices) {
        for (shapeX in patchShape[shapeY].indices) {
            if (x + shapeX >= BOARD_WIDTH) return false
            if (y + shapeY >= BOARD_HEIGHT) return false
            if (board[y + shapeY][x + shapeX] && patchShape[shapeY][shapeX]) return false
        }
    }
    for (shapeY in patchShape.indices) {
        for (shapeX in patchShape[shapeY].indices) {
            board[y + shapeY][x + shapeX] = board[y + shapeY][x + shapeX] or patchShape[shapeY][shapeX]
        }
    }
    return true
}

object League1Winner {
    @JvmStatic
    fun main(args: Array<String>?) {
        val r = kotlin.random.Random(0L)
        val scanner = Scanner(System.`in`)

        val myBoard = Array(BOARD_HEIGHT) { Array(BOARD_WIDTH) { false } }

        val incomeEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }
        val patchEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }

        var specialsPlaced = 0

        gameLoop@ while (true) {
            val myScore = scanner.nextInt()
            val myTime = scanner.nextInt()
            val myEarning = scanner.nextInt()
            scanner.nextLine()
            val board = (0 until 9).map { scanner.nextLine() }
            val oppScore = scanner.nextInt()
            val oppTime = scanner.nextInt()
            val oppEarning = scanner.nextInt()
            scanner.nextLine()
            val opponentBoard = (0 until 9).map { scanner.nextLine() }
            val patches = (0 until scanner.nextInt()).map { scanner.nextPatch() }
            val patch = patches.take(3).maxByOrNull { it.shape.sumOf { it.count { it } } } ?: throw IllegalStateException()
            val bonusPatchId = scanner.nextInt()

            val randomX = (0 until BOARD_WIDTH)
            val randomY = (0 until BOARD_HEIGHT)
            for (x in randomX) {
                for (y in randomY) {
                    if (tryApplyPatchToBoard(myBoard, patch.shape, x, y)) {
                        for (y1 in 0 until BOARD_HEIGHT) {
                            for (x1 in 0 until BOARD_WIDTH) {
                                System.err.print(if (myBoard[y1][x1]) 'O' else '.')
                            }
                            System.err.println()
                        }
                        println("PLAY ${patch.id} $x $y")
                        continue@gameLoop
                    }
                }
            }
            println("SKIP")
        }
    }
}

object Boss1 {

    @JvmStatic
    fun main(args: Array<String>?) {
        val r = kotlin.random.Random(0L)
        val scanner = Scanner(System.`in`)

        val myBoard = Array(BOARD_HEIGHT) { Array(BOARD_WIDTH) { false } }

        val incomeEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }
        val patchEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }

        var specialsPlaced = 0

        gameLoop@ while (true) {
            val myScore = scanner.nextInt()
            val myTime = scanner.nextInt()
            val myEarning = scanner.nextInt()
            scanner.nextLine()
            val board = (0 until 9).map { scanner.nextLine() }
            val oppScore = scanner.nextInt()
            val oppTime = scanner.nextInt()
            val oppEarning = scanner.nextInt()
            scanner.nextLine()
            val opponentBoard = (0 until 9).map { scanner.nextLine() }
            val patches = (0 until scanner.nextInt()).map { scanner.nextPatch() }
            val availablePatches = patches.take(3).shuffled(r)
            val bonusPatchId = scanner.nextInt()

            for (patch in availablePatches) {
                if (patch.price > myScore) {
                    continue
                }

                val randomX = (0 until BOARD_WIDTH).shuffled(r)
                val randomY = (0 until BOARD_HEIGHT).shuffled(r)
                for (x in randomX) {
                    for (y in randomY) {
                        if (tryApplyPatchToBoard(myBoard, patch.shape, x, y)) {
                            for (y1 in 0 until BOARD_HEIGHT) {
                                for (x1 in 0 until BOARD_WIDTH) {
                                    System.err.print(if (myBoard[y1][x1]) 'O' else '.')
                                }
                                System.err.println()
                            }
                            println("PLAY ${patch.id} $x $y")
                            continue@gameLoop
                        }
                    }
                }
            }
            println("SKIP")
        }
    }
}

object Boss2 {

    @JvmStatic
    fun main(args: Array<String>?) {
        val r = kotlin.random.Random(0L)
        val scanner = Scanner(System.`in`)

        val myBoard = Array(BOARD_HEIGHT) { Array(BOARD_WIDTH) { false } }

        val incomeEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }
        val patchEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }

        gameLoop@ while (true) {
            val myScore = scanner.nextInt()
            val myTime = scanner.nextInt()
            val myEarning = scanner.nextInt()
            scanner.nextLine()
            val board = (0 until 9).map { scanner.nextLine() }
            val oppScore = scanner.nextInt()
            val oppTime = scanner.nextInt()
            val oppEarning = scanner.nextInt()
            scanner.nextLine()
            val opponentBoard = (0 until 9).map { scanner.nextLine() }
            val patches = (0 until scanner.nextInt()).map { scanner.nextPatch() }
            val bonusPatchId = scanner.nextInt()
            val availablePatches = if (bonusPatchId == 0) patches.take(3) else listOf(Patch(bonusPatchId, listOf(listOf(true)), 0, 0, 0))

            for (patch in availablePatches) {
                if (patch.price > myScore) { continue }
                val randomX = (0 until BOARD_WIDTH).shuffled(r)
                val randomY = (0 until BOARD_HEIGHT).shuffled(r)
                for (x in randomX) {
                    for (y in randomY) {

                        val rotations = arrayOf(
                            0 to patch.shape,
                            1 to patch.shape.rightRotated,
                            2 to patch.shape.rightRotated.rightRotated,
                            3 to patch.shape.rightRotated.rightRotated.rightRotated,
                            4 to patch.shape.mirrored,
                            5 to patch.shape.mirrored.rightRotated,
                            6 to patch.shape.mirrored.rightRotated.rightRotated,
                            7 to patch.shape.mirrored.rightRotated.rightRotated.rightRotated
                        )

                        rotations.shuffle()

                        val rotation = rotations.firstOrNull { tryApplyPatchToBoard(myBoard, it.second, x, y) }

                        if (rotation != null) {
                            for (y1 in 0 until BOARD_HEIGHT) {
                                for (x1 in 0 until BOARD_WIDTH) {
                                    System.err.print(if (myBoard[y1][x1]) 'O' else '.')
                                }
                                System.err.println()
                            }
                            val mirror = rotation.first >= 4
                            val rightRotations = rotation.first % 4
                            println("PLAY ${patch.id} $x $y ${if (mirror) 1 else 0} $rightRotations")
                            continue@gameLoop
                        }
                    }
                }
            }
            println("SKIP")
        }
    }
}

object Boss3 {

    @JvmStatic
    fun main(args: Array<String>?) {
        val scanner = Scanner(System.`in`)

        val myBoard = Array(BOARD_HEIGHT) { Array(BOARD_WIDTH) { false } }

        val incomeEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }
        val patchEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }

        gameLoop@ while (true) {
            val myScore = scanner.nextInt()
            val myTime = scanner.nextInt()
            val myEarning = scanner.nextInt()
            scanner.nextLine()
            val board = (0 until 9).map { scanner.nextLine() }
            val oppScore = scanner.nextInt()
            val oppTime = scanner.nextInt()
            val oppEarning = scanner.nextInt()
            scanner.nextLine()
            val opponentBoard = (0 until 9).map { scanner.nextLine() }
            val patches = (0 until scanner.nextInt()).map { scanner.nextPatch() }
            val bonusPatchId = scanner.nextInt()
            val availablePatches = if (bonusPatchId == 0) patches.take(3) else listOf(Patch(bonusPatchId, listOf(listOf(true)), 0, 0, 0))

            for (patch in availablePatches) {
                if (patch.price > myScore) { continue }
                for (x in 0 until BOARD_WIDTH) {
                    for (y in 0 until BOARD_HEIGHT) {

                        val rotations = arrayOf(
                            0 to patch.shape,
                            1 to patch.shape.rightRotated,
                            2 to patch.shape.rightRotated.rightRotated,
                            3 to patch.shape.rightRotated.rightRotated.rightRotated,
                            4 to patch.shape.mirrored,
                            5 to patch.shape.mirrored.rightRotated,
                            6 to patch.shape.mirrored.rightRotated.rightRotated,
                            7 to patch.shape.mirrored.rightRotated.rightRotated.rightRotated
                        )

                        rotations.shuffle()

                        val rotation = rotations.firstOrNull { tryApplyPatchToBoard(myBoard, it.second, x, y) }

                        if (rotation != null) {
                            for (y1 in 0 until BOARD_HEIGHT) {
                                for (x1 in 0 until BOARD_WIDTH) {
                                    System.err.print(if (myBoard[y1][x1]) 'O' else '.')
                                }
                                System.err.println()
                            }
                            val mirror = rotation.first >= 4
                            val rightRotations = rotation.first % 4
                            println("PLAY ${patch.id} $x $y ${if (mirror) 1 else 0} $rightRotations")
                            continue@gameLoop
                        }
                    }
                }
            }
            println("SKIP")
        }
    }
}

object Boss4 {

    private const val BOARD_WIDTH = 9
    private const val BOARD_HEIGHT = 9

    data class Patch(
        val id: Int,
        val shape: PatchShape,
        val earn: Int,
        val price: Int,
        val time: Int
    )

    val PatchShape.width: Int get() = get(0).size

    fun Scanner.nextPatch(): Patch {
        val patchId = nextInt()
        val patchEarn = nextInt()
        val patchPrice = nextInt()
        val patchTime = nextInt()
        val shape = next()
        nextLine()
        return Patch(patchId, parseShape(shape), patchEarn, patchPrice, patchTime)
    }

    private fun parseShape(s: String): PatchShape =
        s.split("|").map { it.map { it == 'O' } }

    val PatchShape.mirrored: PatchShape
        get() =
            this.map { it.reversed() }

    val PatchShape.rightRotated: PatchShape
        get() =
            List(width) { row -> this.map { it[row] }.reversed() }

    private fun tryApplyPatchToBoard(board: Array<Array<Boolean>>, patchShape: PatchShape, x: Int, y: Int): Boolean {
        for (shapeY in patchShape.indices) {
            for (shapeX in patchShape[shapeY].indices) {
                if (x + shapeX >= BOARD_WIDTH) return false
                if (y + shapeY >= BOARD_HEIGHT) return false
                if (board[y + shapeY][x + shapeX] && patchShape[shapeY][shapeX]) return false
            }
        }
        for (shapeY in patchShape.indices) {
            for (shapeX in patchShape[shapeY].indices) {
                board[y + shapeY][x + shapeX] = board[y + shapeY][x + shapeX] or patchShape[shapeY][shapeX]
            }
        }
        return true
    }

    fun Array<Array<Boolean>>.findFreeSpace() : Pair<Int, Int>? {
        for (x in 0..8) {
            for (y in 0..8) {
                if (!this[y][x]) return x to y
            }
        }
        return null
    }

    fun Array<Array<Boolean>>.fill(x: Int, y: Int) {
        if (x < 0 || x > 8 || y < 0 || y > 8 || this[y][x]) return
        this[y][x] = true
        fill(x-1, y)
        fill(x+1, y)
        fill(x, y-1)
        fill(x, y+1)
    }

    private fun Array<Array<Boolean>>.countAmountOfHoles(): Int {
        var count = 0
        while(true) {
            val (holeX, holeY) = findFreeSpace()?: break
            count++
            fill(holeX, holeY)
        }
        return count
    }

    fun Array<Array<Boolean>>.copy() = map { it.map { it }.toTypedArray() }.toTypedArray()

    @JvmStatic
    fun main(args: Array<String>?) {
        val scanner = Scanner(System.`in`)

        val incomeEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }
        val patchEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }

        var specialsPlaced = 0

        gameLoop@ while (true) {
            val myScore = scanner.nextInt()
            val myTime = scanner.nextInt()
            val myEarning = scanner.nextInt()
            scanner.nextLine()
            val myBoard = (0 until 9).map { scanner.nextLine().map { it == 'O' }.toTypedArray() }.toTypedArray()
            val oppScore = scanner.nextInt()
            val oppTime = scanner.nextInt()
            val oppEarning = scanner.nextInt()
            scanner.nextLine()
            val opponentBoard = (0 until 9).map { scanner.nextLine() }
            val patches = (0 until scanner.nextInt()).map { scanner.nextPatch() }
            val bonusPatchId = scanner.nextInt()

            val availablePatches = if (bonusPatchId == 0)patches.take(3)
                .filter { it.price <= myScore }
                .sortedByDescending {
                    // it will earn this much buttons whole game
                    val remainingEarningTurns = incomeEvents.count { it > myTime } * it.earn
                    // it will earn this much points towards end score
                    val spaceValue = it.shape.sumOf { it.count() } * 2

                    remainingEarningTurns + spaceValue - it.price - it.time / 2
                } else listOf(Patch(bonusPatchId, listOf(listOf(true)), 0, 0, 0))

            data class Spec(val patchId:Int, val x:Int, val y: Int, val orientation: Int, val flip: Boolean)

            val solutions = mutableListOf<Pair<Spec, Double>>()


            for (patch in availablePatches) {
                for (x in 0 until BOARD_WIDTH) {
                    for (y in 0 until BOARD_HEIGHT) {

                        val rotations = mapOf(
                            (0 to false) to patch.shape,
                            (1 to false) to patch.shape.rightRotated,
                            (2 to false) to patch.shape.rightRotated.rightRotated,
                            (3 to false) to patch.shape.rightRotated.rightRotated.rightRotated,
                            (0 to true) to patch.shape.mirrored,
                            (1 to true) to patch.shape.mirrored.rightRotated,
                            (2 to true) to patch.shape.mirrored.rightRotated.rightRotated,
                            (3 to true) to patch.shape.mirrored.rightRotated.rightRotated.rightRotated
                        )

                        for (flip in arrayOf(true, false)) {
                            for (rotation in arrayOf(0, 1, 2, 3)) {
                                val spec = Spec(patch.id, x, y, rotation, flip)
                                val copy = myBoard.copy()
                                val accepted = tryApplyPatchToBoard(copy, rotations[rotation to flip]!!, x, y)
                                if (accepted) {
                                    solutions.add(spec to copy.countAmountOfHoles().toDouble())
                                }
                            }
                        }
                    }
                }
            }

            System.err.println("Solutions - ${solutions.size}")
            if (solutions.isNotEmpty()) {
                val (s) = solutions.minByOrNull { it.second }!!
                println("PLAY ${s.patchId} ${s.x} ${s.y} ${if (s.flip) 1 else 0} ${s.orientation}")
            } else {
                println("SKIP")
            }
        }
    }

}


object AgentDummy {

    /**
     * Become the master of patchworking by filling your quilt canvas with patches. Be careful! Every patch costs you a few Buttons and it also takes some Time to sew it on your canvas.
     **/
    @JvmStatic
    fun main(args: Array<String>?) {
        val input = Scanner(System.`in`)
        val incomeEvents = input.nextInt() // the amount of "Button income" events that will happen
        for (i in 0 until incomeEvents) {
            val incomeTime = input.nextInt() // when the "Button income" will happen
        }
        val patchEvents = input.nextInt() // the amount of "Special Patch" events that will happen
        for (i in 0 until patchEvents) {
            val patchTime = input.nextInt() // when the "Special Patch" will happen
        }

        // game loop
        while (true) {
            val myButtons = input.nextInt() // how many Buttons you hold right now
            val myTime = input.nextInt() // where is my time token placed on timeline
            val myEarning =
                input.nextInt() // how much will you earn during "Button income" phase with your current quilt board
            for (i in 0 until 9) {
                val line =
                    input.next() // represents row of a board board "O....O.." means, 1st and 6th field is covered by patch on this row
            }
            val opponentButtons = input.nextInt() // how many Buttons your opponent holds right now
            val opponentTime = input.nextInt() // where is opponent time token placed on timeline
            val opponentEarning =
                input.nextInt() // how much will opponent earn during "Button income" phase with his current quilt board
            for (i in 0 until 9) {
                val line = input.next()
            }
            val patches = input.nextInt() // how many patches there are in total in a game
            for (i in 0 until patches) {
                val patchId = input.nextInt()
                val patchEarning = input.nextInt() // how much Buttons you will acquire for this patch when "Button income" timepoint is reached
                val patchButtonPrice = input.nextInt() // how much Buttons does it cost to buy this patch
                val patchTimePrice = input.nextInt() // how much time does it take to sew this patch
                val patchShape = input.next() // representation of patch shape "O.O|OOO|O.O" represents H shaped patch
            }
            val bonusPatchId = input.nextInt() // 0 if no bonus patch is available

            // Write an answer using println()
            // To debug: System.err.println("Debug messages...");

            println("SKIP")
        }
    }
}
