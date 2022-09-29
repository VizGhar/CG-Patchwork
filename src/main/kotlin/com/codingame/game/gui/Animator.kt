package com.codingame.game.gui

import com.codingame.game.*
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule

class Animator(
    private val g: GraphicEntityModule,
    private val gameManager: MultiplayerGameManager<Player>,
    private val gui: Interface,
    private val boardManager: BoardManager
) {

    internal data class Animation(val duration: Int, val action: (Double) -> Unit)

    private fun List<Animation>.run(g: GraphicEntityModule, gameManager: MultiplayerGameManager<Player>) {
        val turnDuration = sumOf { it.duration }
        gameManager.frameDuration = turnDuration
        var spent = 0.0
        forEach {
            val slice = it.duration.toDouble() / turnDuration
            it.action(spent -0.0001)
            spent += slice
            g.commitWorldState(minOf(1.0, spent))
        }
    }

    fun animateGameState(
        activePlayerId: Int,
        move: Move,
        moveResult: TurnResult.OK,
        message: String
    ) {
        val activePlayer = gameManager.players[activePlayerId]

        if (moveResult.bonusAchieved) {
            gameManager.addTooltip(activePlayer, "${activePlayer.nicknameToken} won 7x7 Bonus Button")
        }

        // UI operations
        val animations = mutableListOf<Animation>()
        animations += Animation(1) {
            gui.showMessage(activePlayerId, message)
            gui.showMessage((activePlayerId + 1) % 2, "")
        }

        if (move is Move.Play) {
            // enlarge selected label
            animations += Animation(300) { gui.enlarge(move.patchId) }
            // pulse players button icon for each button spent
            animations += (1..(patches.firstOrNull { it.id == move.patchId }?.price ?: 0)).map {
                listOf(
                    Animation(200) { gui.pulseIn(activePlayerId, -1) },
                    Animation(200) { from -> gui.pulseOut(from, activePlayerId) })
            }.flatten()

            // move patch to proper position
            animations += Animation(500) { from ->
                gui.move(from, activePlayerId, move.patchId, move.x, move.y, move.flip, move.rightRotations)
                gui.updateDebugCoords(from, activePlayerId, boardManager.players[activePlayerId].board)
            }
            animations += Animation(10) { from ->
                gui.updateIncome(
                    from,
                    activePlayerId,
                    boardManager.players[activePlayerId].earning
                )
            }
        }

        // move time token
        if (moveResult.skippedTimepoints == 0) {
            animations += Animation(300) { from ->
                gui.updateTime(
                    from,
                    boardManager.players[0].position,
                    boardManager.players[1].position,
                    false
                )
            }
        } else {
            for (i in 0 until moveResult.skippedTimepoints) {
                animations += Animation(300) { from ->
                    gui.updateTime(
                        from,
                        boardManager.players[0].position,
                        boardManager.players[1].position,
                        true
                    )
                }
                animations += Animation(200) { gui.pulseIn(activePlayerId, +1) }
                animations += Animation(200) { from -> gui.pulseOut(from, activePlayerId) }
            }
        }

        if (moveResult.bonusAchieved) {
            animations += Animation(500) { from -> gui.acquireBonusBegin(from) }
            animations += Animation(500) { gui.acquireBonusMiddle() }
            animations += Animation(500) { gui.acquireBonusEnd(activePlayerId) }
        }

        if (moveResult.earnReached) {
            animations += Animation(600) { from ->
                gui.updateMoney(
                    from,
                    activePlayerId,
                    boardManager.players[0].money,
                    boardManager.players[1].money
                )
            }
            animations += Animation(10) { from -> gui.returnMoney() }
        }

        animations += Animation(1000) { from -> gui.showPatchesBelt(from, boardManager.remainingPatches) }

        animations.run(g, gameManager)
    }
}