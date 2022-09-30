package com.codingame.game

import com.codingame.game.config.*
import com.codingame.gameengine.core.MultiplayerGameManager
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

private const val BOARD_WIDTH = 9
private const val BOARD_HEIGHT = 9

sealed class TurnResult {
    object UnknownCommand: TurnResult()
    object InvalidPatchId: TurnResult()
    object InvalidPatchPlacement: TurnResult()
    object NoMoney: TurnResult()
    object CantSkip: TurnResult()
    data class OK(val bonusAchieved: Boolean, val earnReached: Boolean, val skippedTimepoints: Int): TurnResult()
}

class BoardManager(private val gameManager: MultiplayerGameManager<Player>) {

    private val random by lazy { SecureRandom(gameManager.seed.toString().toByteArray()).asKotlinRandom() }

    /**
     * Shuffled patches for this instance of game.
     * There are 32 shuffled patches + 1 finalPatch always at the end
     */
    var remainingPatches : MutableList<Patch> = (patches.shuffled(random) + finalPatch).toMutableList()

    /**
     * First 3 patches are available for purchase
     */
    private val availablePatches : List<Patch> get() = remainingPatches.take(3)

    /**
     * These are 5 special patches (for league 3 and up)
     */
    val gameBonusPatches = if (league.specialPatchesEnabled) bonusPatches.toMutableList() else mutableListOf()

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
        val playedPatches: MutableList<Patch> = mutableListOf(),
        val board: Array<Array<Boolean>> = Array(9) { Array(9) { false } }
    ) {
        val earning get() = playedPatches.sumOf { it.earn }
    }

    private fun tryApplyPatchToBoard(board: Array<Array<Boolean>>, patchShape: PatchShape, x: Int, y: Int, apply: Boolean = true) : Boolean {
        for (shapeY in patchShape.indices) {
            for (shapeX in patchShape[shapeY].indices) {
                if (x + shapeX >= BOARD_WIDTH) return false
                if (y + shapeY >= BOARD_HEIGHT) return false
                if (board[y + shapeY][x + shapeX] && patchShape[shapeY][shapeX]) return false
            }
        }
        if (!apply) return true
        for (shapeY in patchShape.indices) {
            for (shapeX in patchShape[shapeY].indices) {
                board[y + shapeY][x + shapeX] = board[y + shapeY][x + shapeX] or patchShape[shapeY][shapeX]
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
        players[0].availablePatches > 0 -> 0                    // apply bonus patch player 0
        players[1].availablePatches > 0 -> 1                    // apply bonus patch player 1
        players[0].position == players[1].position -> lastPlay  // stacked players - last played moves
        players[0].position < players[1].position -> 0          // standard player 0
        else -> 1                                               // standard player 1
    }

    fun playPatch(patchId: Int, requiredOrientation: Int, isFlipRequired: Boolean, x: Int, y: Int, apply: Boolean = true) : TurnResult {
        val orientation = if (league.rotationsAllowed) requiredOrientation else 0
        val flip = if (league.rotationsAllowed) isFlipRequired else false
        val playerId = actualPlayerId
        val player = players[playerId]

        // pick patch
        val patch = when {
            player.availablePatches > 0 && patchId != gameBonusPatches[0].id -> return TurnResult.InvalidPatchId
            player.availablePatches > 0 -> gameBonusPatches[0]
            player.availablePatches == 0 && patchId !in availablePatches.map { it.id } -> return TurnResult.InvalidPatchId
            player.availablePatches == 0 -> remainingPatches.first { it.id == patchId }.let { if(league.earnTurns.isEmpty()) it.copy(earn = 0) else it }
            else -> throw IllegalStateException("Ooops this shouldn't happen. Available patches should be non negative integer. Please provide author of this game with this error message and shared replay.")
        }

        // check price vs. money
        if (patch.price > player.money) return TurnResult.NoMoney

        // place patch on player's board
        val shape = patch.shape.all[(if (flip) 4 else 0) + orientation]
        val patchPlaced = tryApplyPatchToBoard(player.board, shape, x, y, apply)
        if (!patchPlaced) return TurnResult.InvalidPatchPlacement
        if (!apply) return TurnResult.OK(bonusAchieved = false, earnReached = false, skippedTimepoints = 0)
        // !! patch placed on board Successfully !!

        // store played patch
        player.playedPatches.add(patch)

        // pay for patch and move player's token
        val timeDelta = minOf(patch.time, league.gameDuration - player.position)
        player.money -= patch.price
        val earnReached = timeAdvance(player, timeDelta)
        lastPlay = playerId

        // adjust remaining patches
        if (gameBonusPatches.isEmpty() || patch.id != gameBonusPatches[0].id) {
            val jumpBy = availablePatches.indexOfFirst{ patch.id == it.id}
            remainingPatches = (remainingPatches.drop(jumpBy + 1) + remainingPatches.take(jumpBy)).toMutableList()
        }

        // remove bonus patch if it was played now
        if (gameBonusPatches.isNotEmpty() && patch.id == gameBonusPatches[0].id) {
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

        if (player.availablePatches > 0) return TurnResult.CantSkip

        val delta = minOf(
            opponent.position - player.position + 1,
            league.gameDuration - player.position
        )

        player.money += delta * league.skipMultiplier
        val earnReached = timeAdvance(player, delta)
        lastPlay = playerId

        return TurnResult.OK(false, earnReached, delta * league.skipMultiplier)
    }

    private fun timeAdvance(player: PlayerData, positionDelta: Int): Boolean {
        val positionBefore = player.position
        player.position += positionDelta
        val positionAfter = player.position

        // resolve Button Income
        val earning = league.earnTurns.count { it in (positionBefore + 1)..positionAfter } * player.playedPatches.sumOf { it.earn }
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
            val minusPoints = playerData.board.sumOf { row -> row.count { taken -> !taken } }   * -2                                     // 0 for league 1. -2 otherwise
            val money = playerData.money                                                        * league.scoreMoneyMultiplier            // 0 for league 1. 1 otherwise
            val bonusPoints = (if (playerData.bonusAchieved) 1 else 0)                          * league.scoreBonusMultiplier            // 7 for league 3. 0 otherwise
            200 + bonusPoints + money + minusPoints
        }.let {
            // draw - player who finishes first win
            when {
                it[0] == it[1] && players[0].finishedFirst -> listOf(it[0] + 1, it[1])
                it[0] == it[1] && players[1].finishedFirst -> listOf(it[0], it[1] + 1)
                else -> it
            }
        }

}