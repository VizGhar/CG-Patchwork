package com.codingame.game

import com.google.common.annotations.VisibleForTesting

const val TOTAL_TURNS = 53 // starting position + 53 squares
val EARNING_TURNS = listOf(5, 11, 17, 23, 29, 35, 41, 47, 53)
val PATCH_TURNS = listOf(20, 26, 32, 44, 50)
private const val BOARD_WIDTH = 9
private const val BOARD_HEIGHT = 9
private const val MINUS_POINTS_MULTIPLIER = 2
private const val BONUS_POINTS_MULTIPLIER = 7

enum class TurnResult {
    UNKNOWN_COMMAND,
    INVALID_TILE_ID,
    INVALID_TILE_PLACEMENT,
    NO_MONEY,
    CANT_SKIP,
    OK
}

class BoardManager(preparedTiles: List<Tile>, private val gui: Interface) {

    val gameTiles = preparedTiles.toMutableList()

    @VisibleForTesting
    class PlayerData(
        var money: Int = 5,
        var position: Int = 0,
        var availablePatches: Int = 0,
        var bonusAchieved: Boolean = false,
        val playedTiles: MutableList<Tile> = mutableListOf(),
        val board: Array<Array<Boolean>> = Array(9) { Array(9) { false } }
    )

    private fun tryApplyTileToBoard(board: Array<Array<Boolean>>, tileShape: TileShape, x: Int, y: Int) : Boolean {
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

    @VisibleForTesting val players = arrayOf(PlayerData(), PlayerData())
    private var lastPlay = 0
    private val takenPatches = mutableListOf<Int>()

    private var neutralTokenPosition = 0

    val availableTiles : List<Tile> get() = orderedGameTiles.take(3)

    val orderedGameTiles get() = gameTiles.subList(neutralTokenPosition, gameTiles.size) + gameTiles.subList(0, neutralTokenPosition)

    val gameBonusTiles = bonusTiles.toMutableList()

    val actualPlayerId get() = when {
        players[0].availablePatches > 0 -> 0                    // apply patch 0
        players[1].availablePatches > 0 -> 1                    // apply patch 1
        players[0].position == players[1].position -> lastPlay  // stacked players
        players[0].position < players[1].position -> 0          // standard 0
        else -> 1                                               // standard 1
    }

    val actualPlayerPosition get() = players[actualPlayerId].position
    val actualPlayerMoney get() = players[actualPlayerId].money

    fun move(tileid: Int, orientation: Int, mirrored: Boolean, x: Int, y: Int) : TurnResult {
        val playerId = actualPlayerId
        val player = players[playerId]

        // pick tile
        val tile = when {
            player.availablePatches > 0 && tileid != gameBonusTiles[0].id -> return TurnResult.INVALID_TILE_ID
            player.availablePatches > 0 -> gameBonusTiles[0]
            player.availablePatches == 0 && tileid !in availableTiles.map { it.id } -> return TurnResult.INVALID_TILE_ID
            player.availablePatches == 0 -> tiles[tileid]
            else -> throw IllegalStateException("Ooops this shouldn't happen. Available patches should be non negative integer. Please provide author of this game with this error message and shared replay.")
        }

        // check price vs. money
        if (tile.price > player.money) return TurnResult.NO_MONEY

        // place tile on player's board
        val shape = tile.shape.all[(if (mirrored) 4 else 0) + orientation]
        val tilePlaced = tryApplyTileToBoard(player.board, shape, x, y)
        if (!tilePlaced) return TurnResult.INVALID_TILE_PLACEMENT

        // !! tile placed on board Successfully !!

        // store played tile
        player.playedTiles.add(tile)

        // pay for tile and move player's token
        val timeDelta = minOf(tile.time, TOTAL_TURNS - player.position)
        move(player, -tile.price, timeDelta)
        lastPlay = playerId

        // move neutral token (if applicable)
        if (gameBonusTiles.isEmpty() || tile.id != gameBonusTiles[0].id) {
            neutralTokenPosition = (neutralTokenPosition + availableTiles.indexOf(tile)) % (gameTiles.size - 1)
        }

        if (gameBonusTiles.isNotEmpty() && tile.id == gameBonusTiles[0].id) {
            gameBonusTiles.removeAt(0)
            player.availablePatches--
        }

        // remove tile from stack
        gameTiles.remove(tile)
        gui.move(playerId, tileid, x, y, mirrored, orientation)
        return TurnResult.OK
    }

    fun skip(): TurnResult {
        val playerId = actualPlayerId
        val player = players[playerId]
        val opponent = players[(playerId + 1) % 2]

        // cannot skip if applying patch
        if (player.availablePatches > 0) return TurnResult.CANT_SKIP

        val delta = minOf(
            // add 1 button and 1 time for each position from actual position to position in front of opponent
            opponent.position - player.position + 1,
            // but do not move token outside board
            TOTAL_TURNS - player.position
        )

        move(player, delta, delta)
        lastPlay = playerId

        return TurnResult.OK
    }

    private fun move(player: PlayerData, moneyDelta: Int, positionDelta: Int) {
        val positionBefore = player.position
        player.money += moneyDelta
        player.position += positionDelta
        val positionAfter = player.position

        val earning = EARNING_TURNS.count { it in (positionBefore + 1)..positionAfter } * player.playedTiles.sumOf { it.earn }
        player.money += earning
        val patches = PATCH_TURNS.filter { it in (positionBefore + 1)..positionAfter && it !in takenPatches }
        takenPatches.addAll(patches)
        player.availablePatches += patches.size
    }

    val remainingTurns get() = TOTAL_TURNS - actualPlayerPosition

    val earningPhases get() = EARNING_TURNS.map { TOTAL_TURNS - actualPlayerPosition }

    val patchEarnings get() = PATCH_TURNS.map { TOTAL_TURNS - actualPlayerPosition }

    fun score() =
        players.map { playerData ->
            val minusPoints = playerData.board.sumOf { row -> row.count { taken -> taken } } * MINUS_POINTS_MULTIPLIER
            val money = playerData.money
            val bonusPoints = if (playerData.bonusAchieved) BONUS_POINTS_MULTIPLIER else 0
            bonusPoints + money + minusPoints
        }
}