package com.codingame.game

import com.google.common.annotations.VisibleForTesting

private const val BOARD_WIDTH = 9
private const val BOARD_HEIGHT = 9

enum class TurnResult {
    UNKNOWN_COMMAND,
    INVALID_TILE_ID,
    INVALID_TILE_PLACEMENT,
    NO_MONEY,
    OK
}

class BoardManager(preparedTiles: List<Tile>, private val gui: Interface) {

    val gameTiles = preparedTiles.toMutableList()

    @VisibleForTesting
    class PlayerData(
        var money: Int = league.initialButtons,
        var position: Int = 0,
        var availablePatches: Int = 0,
        var bonusAchieved: Boolean = false,
        var finishedFirst: Boolean = false,
        val playedTiles: MutableList<PlayedTile> = mutableListOf(),
        val board: Array<Array<Boolean>> = Array(9) { Array(9) { false } }
    )

    class PlayedTile(
        val tile: Tile,
        val positionX: Int,
        val positionY: Int,
        val orientation: Int,
        val mirrored: Boolean
    ) {
        override fun toString(): String {
            return "$tile $orientation $mirrored $positionX $positionY"
        }
    }

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

    val gameBonusTiles = if (league.scoreBonusMultiplier > 0) bonusTiles.toMutableList() else mutableListOf()

    val actualPlayerId get() = when {
        players[0].availablePatches > 0 -> 0                    // apply patch 0
        players[1].availablePatches > 0 -> 1                    // apply patch 1
        players[0].position == players[1].position -> lastPlay  // stacked players
        players[0].position < players[1].position -> 0          // standard 0
        else -> 1                                               // standard 1
    }

    val actualPlayerPosition get() = players[actualPlayerId].position
    val actualPlayerMoney get() = players[actualPlayerId].money

    fun move(tileId: Int, requiredOrientation: Int, isFlipRequired: Boolean, x: Int, y: Int) : TurnResult {
        val orientation = if (league.rotationsAllowed) requiredOrientation else 0
        val flip = if (league.rotationsAllowed) isFlipRequired else false
        val playerId = actualPlayerId
        val player = players[playerId]

        // pick tile
        val tile = when {
            player.availablePatches > 0 && tileId != gameBonusTiles[0].id -> return TurnResult.INVALID_TILE_ID
            player.availablePatches > 0 -> gameBonusTiles[0]
            player.availablePatches == 0 && tileId !in availableTiles.map { it.id } -> return TurnResult.INVALID_TILE_ID
            player.availablePatches == 0 -> tiles[tileId].let { if(league.earnTurns.isEmpty()) it.copy(earn = 0) else it }
            else -> throw IllegalStateException("Ooops this shouldn't happen. Available patches should be non negative integer. Please provide author of this game with this error message and shared replay.")
        }

        // check price vs. money
        if (tile.price > player.money) return TurnResult.NO_MONEY

        // place tile on player's board
        val shape = tile.shape.all[(if (flip) 4 else 0) + orientation]
        val tilePlaced = tryApplyTileToBoard(player.board, shape, x, y)
        if (!tilePlaced) return TurnResult.INVALID_TILE_PLACEMENT

        // !! tile placed on board Successfully !!

        // store played tile
        player.playedTiles.add(PlayedTile(tile, x, y, orientation, flip))

        // pay for tile and move player's token
        val timeDelta = minOf(tile.time, TOTAL_TURNS - player.position)
        move(player, -tile.price, timeDelta)
        lastPlay = playerId

        // move neutral token (if applicable)
        if (gameBonusTiles.isEmpty() || tile.id != gameBonusTiles[0].id) {
            neutralTokenPosition = (neutralTokenPosition + availableTiles.indexOf(tile)) % (gameTiles.size - 1)
        }

        // remove bonus tile if it was played now
        if (gameBonusTiles.isNotEmpty() && tile.id == gameBonusTiles[0].id) {
            gameBonusTiles.removeAt(0)
            player.availablePatches--
        }

        // remove tile from stack
        gameTiles.remove(tile)

        // check bonus
        if (players.none { it.bonusAchieved }) {
            out@ for (startX in 0..2) {
                u@ for (startY in 0..2) {
                    for (windowX in startX..startX+6) {
                        for (windowY in startY..startY+6) {
                            if (!player.board[windowX][windowY]) continue@u
                        }
                    }
                    player.bonusAchieved = true
                    gui.acquireBonus(playerId)
                    break@out
                }
            }
        }

        // UI operations
        gui.move(playerId, tileId, x, y, flip, orientation)

        return TurnResult.OK
    }

    fun skip(): TurnResult {
        val playerId = actualPlayerId
        val player = players[playerId]
        val opponent = players[(playerId + 1) % 2]

        // If SKIP called - apply patch automatically to first free space
        if (player.availablePatches > 0) {
            for (y in 0 until 9) {
                for (x in 0 until 9) {
                    if (!player.board[y][x]) {
                        return move(gameBonusTiles[0].id, 0, false, x, y)
                    }
                }
            }
        }

        val delta = minOf(
            // add 1 button and 1 time for each position from actual position to position in front of opponent
            opponent.position - player.position + 1,
            // but do not move token outside board
            TOTAL_TURNS - player.position
        )

        move(player, delta * league.skipMultiplier, delta)
        lastPlay = playerId

        return TurnResult.OK
    }

    private fun move(player: PlayerData, moneyDelta: Int, positionDelta: Int) {
        val positionBefore = player.position
        player.money += moneyDelta
        player.position += positionDelta
        val positionAfter = player.position

        val earning = league.earnTurns.count { it in (positionBefore + 1)..positionAfter } * player.playedTiles.sumOf { it.tile.earn }
        player.money += earning
        val patches = league.patchTurns.filter { it in (positionBefore + 1)..positionAfter && it !in takenPatches }
        takenPatches.addAll(patches)
        player.availablePatches += patches.size

        if (positionAfter >= TOTAL_TURNS && players.none { it.finishedFirst }) {
            player.finishedFirst = true
        }
    }

    val remainingTurns get() = TOTAL_TURNS - actualPlayerPosition

    val earningPhases get() = league.earnTurns.map { TOTAL_TURNS - actualPlayerPosition }

    val patchEarnings get() = league.patchTurns.map { TOTAL_TURNS - actualPlayerPosition }

    fun score() =
        players.map { playerData ->
            // standard scoring
            val minusPoints = playerData.board.sumOf { row -> row.count { taken -> !taken } }   * league.scoreMinusPointsMultiplier      // 0 for league 0. -2 otherwise
            val plusPoints = playerData.board.sumOf { row -> row.count { taken -> taken } }     * league.scoreFilledMultiplier           // 1 for league 1. 0 otherwise
            val money = playerData.money                                                        * league.scoreMoneyMultiplier            // 0 for league 1. 1 otherwise
            val bonusPoints = (if (playerData.bonusAchieved) 1 else 0)                          * league.scoreBonusMultiplier            // 7 for league 3. 0 otherwise
            200 + plusPoints + bonusPoints + money + minusPoints
        }.let {
            // draw - player who finishes first win
            when {
                it[0] == it[1] && players[0].finishedFirst -> listOf(it[0] + 1, it[1])
                it[0] == it[1] && players[1].finishedFirst -> listOf(it[0], it[1] + 1)
                else -> it
            }
        }
}