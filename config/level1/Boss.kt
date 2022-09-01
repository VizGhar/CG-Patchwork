import java.util.*

typealias TileShape = List<List<Boolean>>

private const val BOARD_WIDTH = 9
private const val BOARD_HEIGHT = 9

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

fun main(args: Array<String>?) {
    val scanner = Scanner(System.`in`)

    val myBoard = Array(BOARD_HEIGHT) { Array(BOARD_WIDTH) { false } }

    val incomeEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }
    val patchEvents = (0 until scanner.nextInt()).map { scanner.nextInt() }
    val tiles = (0 until scanner.nextInt()).map { scanner.nextTile() }

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
        val availableTiles = (0 until scanner.nextInt()).map { scanner.nextTile() }

        for (tile in availableTiles) {
            if (tile.price > myScore) { continue }
            for (x in 0 until BOARD_WIDTH) {
                for (y in 0 until BOARD_HEIGHT) {
                    if (tryApplyTileToBoard(myBoard, tile.shape, x, y)) {
                        for (y1 in 0 until BOARD_HEIGHT) {
                            for (x1 in 0 until BOARD_WIDTH) {
                                System.err.print(if (myBoard[y1][x1]) 'O' else '.')
                            }
                            System.err.println()
                        }
                        println("PLAY ${tile.id} 0 0 $x $y")
                        continue@gameLoop
                    }
                }
            }
        }
        println("SKIP")
    }
}