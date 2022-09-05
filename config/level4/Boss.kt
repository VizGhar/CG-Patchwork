import java.util.*

private const val BOARD_WIDTH = 9
private const val BOARD_HEIGHT = 9

typealias TileShape = List<List<Boolean>>

data class Tile(
    val id: Int,
    val shape: TileShape,
    val earn: Int,
    val price: Int,
    val time: Int
)

val TileShape.width: Int get() = get(0).size

fun Scanner.nextTile(): Tile {
    val tileId = nextInt()
    val tileEarn = nextInt()
    val tilePrice = nextInt()
    val tileTime = nextInt()
    val shape = next()
    nextLine()
    return Tile(tileId, parseShape(shape), tileEarn, tilePrice, tileTime)
}

private fun parseShape(s: String): TileShape =
    s.split("|").map { it.map { it == 'O' } }

val TileShape.mirrored: TileShape
    get() =
        this.map { it.reversed() }

val TileShape.rightRotated: TileShape
    get() =
        List(width) { row -> this.map { it[row] }.reversed() }

private fun tryApplyTileToBoard(board: Array<Array<Boolean>>, tileShape: TileShape, x: Int, y: Int): Boolean {
    for (shapeY in tileShape.indices) {
        for (shapeX in tileShape[shapeY].indices) {
            if (x + shapeX >= BOARD_WIDTH) return false
            if (y + shapeY >= BOARD_HEIGHT) return false
            if (board[y + shapeY][x + shapeX] && tileShape[shapeY][shapeX]) return false
        }
    }
    for (shapeY in tileShape.indices) {
        for (shapeX in tileShape[shapeY].indices) {
            board[y + shapeY][x + shapeX] = board[y + shapeY][x + shapeX] or tileShape[shapeY][shapeX]
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
        val tiles = (0 until scanner.nextInt()).map { scanner.nextTile() }
        val bonusTileId = scanner.nextInt()

        val availableTiles = if (bonusTileId == 0)tiles.take(3)
            .filter { it.price <= myScore }
            .sortedByDescending {
                // it will earn this much buttons whole game
                val remainingEarningTurns = incomeEvents.count { it > myTime } * it.earn
                // it will earn this much points towards end score
                val spaceValue = it.shape.sumOf { it.count() } * 2

                remainingEarningTurns + spaceValue - it.price - it.time / 2
            } else listOf(Tile(bonusTileId, listOf(listOf(true)), 0, 0, 0))

        data class Spec(val tileId:Int, val x:Int, val y: Int, val orientation: Int, val flip: Boolean)

        val solutions = mutableListOf<Pair<Spec, Double>>()


        for (tile in availableTiles) {
            for (x in 0 until BOARD_WIDTH) {
                for (y in 0 until BOARD_HEIGHT) {

                    val rotations = mapOf(
                        (0 to false) to tile.shape,
                        (1 to false) to tile.shape.rightRotated,
                        (2 to false) to tile.shape.rightRotated.rightRotated,
                        (3 to false) to tile.shape.rightRotated.rightRotated.rightRotated,
                        (0 to true) to tile.shape.mirrored,
                        (1 to true) to tile.shape.mirrored.rightRotated,
                        (2 to true) to tile.shape.mirrored.rightRotated.rightRotated,
                        (3 to true) to tile.shape.mirrored.rightRotated.rightRotated.rightRotated
                    )

                    for (flip in arrayOf(true, false)) {
                        for (rotation in arrayOf(0, 1, 2, 3)) {
                            val spec = Spec(tile.id, x, y, rotation, flip)
                            val copy = myBoard.copy()
                            val accepted = tryApplyTileToBoard(copy, rotations[rotation to flip]!!, x, y)
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
            println("PLAY ${s.tileId} ${s.x} ${s.y} ${if (s.flip) 1 else 0} ${s.orientation}")
        } else {
            println("SKIP")
        }
    }
}