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

fun Scanner.Patch(): Patch {
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
