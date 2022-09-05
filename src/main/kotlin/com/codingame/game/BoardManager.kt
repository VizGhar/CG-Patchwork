package com.codingame.game

import kotlin.random.Random

private const val BOARD_WIDTH = 9
private const val BOARD_HEIGHT = 9

sealed class TurnResult {
    object UnknownCommand: TurnResult()
    object InvalidTileId: TurnResult()
    object InvalidTilePlacement: TurnResult()
    object NoMoney: TurnResult()
    data class OK(val bonusAchieved: Boolean, val earnReached: Boolean, val skippedTimepoints: Int): TurnResult()
}

class BoardManager(random: Random) {

    /**
     * Shuffled patches for this instance of game.
     * There are 32 shuffled patches + 1 finalTile always at the end
     */
    var remainingPatches : MutableList<Tile> = (tiles.shuffled(random) + finalTile).toMutableList()

    /**
     * First 3 patches are available for purchase
     */
    private val availablePatches : List<Tile> get() = remainingPatches.take(3)

    /**
     * These are 5 special patches (for league 3 and up)
     */
    val gameBonusPatches = if (league.specialPatchesEnabled) bonusTiles.toMutableList() else mutableListOf()

    /**
     * Data about both players
     */
    val players = arrayOf(PlayerData(), PlayerData())

    /**
     * Last move was made by player with this id. The variable is only used in case both players
     * stands on same time-point
     */
    private var lastPlay = 0

    /**
     * Info about current player
     */
    class PlayerData(
        var money: Int = league.initialButtons,
        var position: Int = 0,
        var availablePatches: Int = 0,
        var bonusAchieved: Boolean = false,
        var finishedFirst: Boolean = false,
        val playedTiles: MutableList<Tile> = mutableListOf(),
        val board: Array<Array<Boolean>> = Array(9) { Array(9) { false } }
    ) {
        val earning get() = playedTiles.sumOf { it.earn }
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

    /**
     * Player that should take move now. This getter resolves, whether any player has any available patch in
     * his hand, or in case when both players are standing on same position, which one should move based on
     * lastPlay var
     */
    val actualPlayerId get() = when {
        players[0].availablePatches > 0 -> 0                    // apply patch 0
        players[1].availablePatches > 0 -> 1                    // apply patch 1
        players[0].position == players[1].position -> lastPlay  // stacked players
        players[0].position < players[1].position -> 0          // standard 0
        else -> 1                                               // standard 1
    }

    fun playTile(tileId: Int, requiredOrientation: Int, isFlipRequired: Boolean, x: Int, y: Int) : TurnResult {
        val orientation = if (league.rotationsAllowed) requiredOrientation else 0
        val flip = if (league.rotationsAllowed) isFlipRequired else false
        val playerId = actualPlayerId
        val player = players[playerId]

        // pick tile
        val tile = when {
            player.availablePatches > 0 && tileId != gameBonusPatches[0].id -> return TurnResult.InvalidTileId
            player.availablePatches > 0 -> gameBonusPatches[0]
            player.availablePatches == 0 && tileId !in availablePatches.map { it.id } -> return TurnResult.InvalidTileId
            player.availablePatches == 0 -> remainingPatches.first { it.id == tileId }.let { if(league.earnTurns.isEmpty()) it.copy(earn = 0) else it }
            else -> throw IllegalStateException("Ooops this shouldn't happen. Available patches should be non negative integer. Please provide author of this game with this error message and shared replay.")
        }

        // check price vs. money
        if (tile.price > player.money) return TurnResult.NoMoney

        // place tile on player's board
        val shape = tile.shape.all[(if (flip) 4 else 0) + orientation]
        val tilePlaced = tryApplyTileToBoard(player.board, shape, x, y)
        if (!tilePlaced) return TurnResult.InvalidTilePlacement

        // !! tile placed on board Successfully !!

        // store played tile
        player.playedTiles.add(tile)

        // pay for tile and move player's token
        val timeDelta = minOf(tile.time, league.gameDuration - player.position)
        player.money -= tile.price
        val earnReached = timeAdvance(player, timeDelta)
        lastPlay = playerId

        // adjust remaining patches
        if (gameBonusPatches.isEmpty() || tile.id != gameBonusPatches[0].id) {
            val jumpBy = availablePatches.indexOfFirst{ tile.id == it.id}
            remainingPatches = (remainingPatches.drop(jumpBy + 1) + remainingPatches.take(jumpBy)).toMutableList()
        }

        // remove bonus tile if it was played now
        if (gameBonusPatches.isNotEmpty() && tile.id == gameBonusPatches[0].id) {
            gameBonusPatches.removeAt(0)
            player.availablePatches--
        }

        var bonusAchieved = false

        // check bonus
        if (players.none { it.bonusAchieved }) {
            out@ for (startX in 0..9 - BONUS_BUTTON_SIZE) {
                u@ for (startY in 0..9 - BONUS_BUTTON_SIZE) {
                    for (windowX in startX until startX + BONUS_BUTTON_SIZE) {
                        for (windowY in startY until startY + BONUS_BUTTON_SIZE) {
                            if (!player.board[windowX][windowY]) continue@u
                        }
                    }
                    player.bonusAchieved = true
                    bonusAchieved = true
                    break@out
                }
            }
        }
        return TurnResult.OK(bonusAchieved, earnReached, 0)
    }

    fun playSkip(): TurnResult {
        val playerId = actualPlayerId
        val player = players[playerId]
        val opponent = players[(playerId + 1) % 2]

        // If SKIP called - apply patch automatically to first free space
        if (player.availablePatches > 0) {
            for (y in 0 until 9) {
                for (x in 0 until 9) {
                    if (!player.board[y][x]) {
                        return playTile(gameBonusPatches[0].id, 0, false, x, y)
                    }
                }
            }
        }

        val delta = minOf(
            opponent.position - player.position + 1,
            league.gameDuration - player.position
        )

        player.money += delta * league.skipMultiplier
        val earnReached = timeAdvance(player, delta)
        lastPlay = playerId

        return TurnResult.OK(false, earnReached, delta)
    }

    private fun timeAdvance(player: PlayerData, positionDelta: Int): Boolean {
        val positionBefore = player.position
        player.position += positionDelta
        val positionAfter = player.position

        // resolve Button Income
        val earning = league.earnTurns.count { it in (positionBefore + 1)..positionAfter } * player.playedTiles.sumOf { it.earn }
        val success = earning > 0
        player.money += earning

        // resolve Special Patch
        val patches = league.patchTurns.filter { it in (positionBefore + 1)..positionAfter && it in gameBonusPatches.map { -it.id } }
        player.availablePatches += patches.size

        // resolve player Finishes first
        if (positionAfter >= league.gameDuration && players.none { it.finishedFirst }) {
            player.finishedFirst = true
        }
        return success
    }

    fun computeScore() =
        players.map { playerData ->
            // standard scoring
            val minusPoints = playerData.board.sumOf { row -> row.count { taken -> !taken } }   * league.scoreMinusPointsMultiplier      // 0 for league 1. -2 otherwise
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