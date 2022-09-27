package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.endscreen.EndScreenModule
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.google.inject.Inject
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

sealed class Move {
    object Skip : Move()
    data class Play(val patchId: Int, val x: Int, val y: Int, val flip: Boolean, val rightRotations: Int): Move()
    object Unknown: Move()
}
@Suppress("unused")
class Referee : AbstractReferee() {

    @Inject private lateinit var g: GraphicEntityModule
    @Inject private lateinit var gameManager: MultiplayerGameManager<Player>
    @Inject private lateinit var endScreenModule: EndScreenModule
    @Inject private lateinit var gui: Interface

    private val random by lazy { SecureRandom(gameManager.seed.toString().toByteArray()).asKotlinRandom() }
    private val boardManager by lazy { BoardManager(random) }

    override fun init() {
        League.leagueInit(gameManager.leagueLevel)  // init game
        take(gameManager.gameParameters)            // override game settings with expert mode rules
        gameManager.firstTurnMaxTime = 1000

        gui.initialize(
            gameManager.players[0],
            gameManager.players[1],
            boardManager.players[0],
            boardManager.players[1],
            boardManager.remainingPatches,
            boardManager.gameBonusPatches
        )

        gameManager.turnMaxTime = 100
        gameManager.maxTurns = 80
        gameManager.frameDuration = 600
    }

    override fun gameTurn(turn: Int) {
        val activePlayerId = boardManager.actualPlayerId
        val activePlayer = gameManager.players[activePlayerId]

        val boardMe = boardManager.players[activePlayerId]
        val boardOpponent = boardManager.players[(activePlayerId + 1) % 2]

        // player's first turn
        if (turn <= gameManager.playerCount) {
            activePlayer.sendInputLine(league.earnTurns.size.toString())
            activePlayer.sendInputLine(league.earnTurns.joinToString(" "))
            activePlayer.sendInputLine(league.patchTurns.size.toString())
            activePlayer.sendInputLine(league.patchTurns.joinToString(" "))
        }

        // every turn (including first one)
        activePlayer.sendInputLine("${boardMe.money} ${boardMe.position} ${boardMe.playedPatches.sumOf { it.earn }}")
        for (i in 0..8) {
            activePlayer.sendInputLine(boardMe.board[i].joinToString("") { if (it) "O" else "." })
        }

        activePlayer.sendInputLine("${boardOpponent.money} ${boardOpponent.position} ${boardOpponent.playedPatches.sumOf { it.earn }}")
        for (i in 0..8) {
            activePlayer.sendInputLine(boardOpponent.board[i].joinToString("") { if (it) "O" else "." })
        }

        activePlayer.sendInputLine(boardManager.remainingPatches.size.toString())
        for (patch in boardManager.remainingPatches) {
            activePlayer.sendInputLine(patch.toString())
        }

        if (boardManager.players[activePlayerId].availablePatches != 0) {
            activePlayer.sendInputLine(boardManager.gameBonusPatches[0].id.toString())
        } else {
            activePlayer.sendInputLine("0")
        }

        activePlayer.execute()

        try {
            val outputs = activePlayer.outputs
            if (outputs.size != 1) { activePlayer.deactivate(String.format("%d Single line of input expected", activePlayer.index)); return }

            val output = outputs[0].split(" ")
            val message: String
            var move = when {
                output[0] == "SKIP" -> {
                    message = output.drop(1).joinToString(" ")
                    boardManager.provideAutoPlaceBonusPatch()
                }
                output[0] == "PLAY" -> {
                    val patchId = output.getOrNull(1)?.toIntOrNull()
                    val x = output.getOrNull(2)?.toIntOrNull()
                    val y = output.getOrNull(3)?.toIntOrNull()
                    val flip = if (!league.rotationsAllowed) null else output.getOrNull(4)?.toIntOrNull()
                    val rightRotations = if (!league.rotationsAllowed) null else output.getOrNull(5)?.toIntOrNull()

                    if (patchId == null || x == null || y == null) {
                        message = ""
                        Move.Unknown
                    } else {
                        message = if (flip == null) {
                            output.drop(4).joinToString(" ")
                        } else if (rightRotations == null) {
                            output.drop(5).joinToString(" ")
                        } else {
                            output.drop(6).joinToString(" ")
                        }

                        Move.Play(
                            patchId = patchId,
                            x = x,
                            y = y,
                            flip = flip == 1,
                            rightRotations = (rightRotations ?: 0) % 4
                        )
                    }
                }
                else -> {
                    message = ""
                    Move.Unknown
                }
            }

            var moveResult = getMoveResult(move)

            when(moveResult){
                TurnResult.UnknownCommand -> activePlayer.deactivate(String.format("$%d unknown command", activePlayer.index))
                TurnResult.InvalidPatchId -> {
                    gameManager.addTooltip(activePlayer, "${activePlayer.nicknameToken} cannot pick this patch - calling SKIP instead")
                    move = boardManager.provideAutoPlaceBonusPatch()
                    moveResult = getMoveResult(move)
                }
                TurnResult.InvalidPatchPlacement -> {
                    gameManager.addTooltip(activePlayer, "${activePlayer.nicknameToken} cannot place patch there - calling SKIP instead")
                    move = boardManager.provideAutoPlaceBonusPatch()
                    moveResult = getMoveResult(move)
                }
                TurnResult.NoMoney -> {
                    gameManager.addTooltip(activePlayer, "${activePlayer.nicknameToken} cannot afford to buy required patch - calling SKIP instead")
                    move = boardManager.provideAutoPlaceBonusPatch()
                    moveResult = getMoveResult(move)
                }
                is TurnResult.OK -> {}
            }

            if (moveResult !is TurnResult.OK) {
                boardManager.computeScore().forEachIndexed { index, score -> gameManager.players[index].score = score }
                activePlayer.score = -1
                gameManager.endGame()
                return
            }

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
                animations += Animation(10) { from -> gui.updateIncome(from, activePlayerId, boardManager.players[activePlayerId].earning) }
            }

            // move time token
            if (moveResult.skippedTimepoints == 0) {
                animations += Animation(300) { from -> gui.updateTime(from, boardManager.players[0].position, boardManager.players[1].position, false) }
            } else {
                for (i in 0 until moveResult.skippedTimepoints) {
                    animations += Animation(300) { from -> gui.updateTime(from, boardManager.players[0].position, boardManager.players[1].position, true) }
                    animations += Animation(200) { gui.pulseIn(activePlayerId, +1) }
                    animations += Animation(200) { from -> gui.pulseOut(from, activePlayerId) }
                }
            }

            if (moveResult.bonusAchieved) {
                animations += Animation(500) { from -> gui.acquireBonusBegin(from) }
                animations += Animation(500) { gui.acquireBonusMiddle()}
                animations += Animation(500) { gui.acquireBonusEnd(activePlayerId) }
            }

            if (moveResult.earnReached) {
                animations += Animation(600) { from -> gui.updateMoney(from, activePlayerId, boardManager.players[0].money, boardManager.players[1].money) }
                animations += Animation(10) { from -> gui.returnMoney() }
            }

            animations += Animation(1000) { from -> gui.showPatchesBelt(from, boardManager.remainingPatches) }

            animations.run(g, gameManager)

            // End game
            if (boardManager.players.any { it.position < league.gameDuration }) { return }
            boardManager.computeScore().forEachIndexed { index, score -> gameManager.players[index].score = score }
        } catch (e: AbstractPlayer.TimeoutException) {
            boardManager.computeScore().forEachIndexed { index, score -> gameManager.players[index].score = score }
            activePlayer.score = -1
            gameManager.endGame()
            activePlayer.deactivate(String.format("$%d timeout!", activePlayer.index))
        }
        gameManager.endGame()
    }

    private fun getMoveResult(move: Move): TurnResult =
        when(move) {
            is Move.Play -> boardManager.playPatch(move.patchId, move.rightRotations, move.flip, move.x, move.y)
            Move.Skip -> boardManager.playSkip()
            Move.Unknown -> TurnResult.UnknownCommand
        }

    override fun onEnd() {
        endScreenModule.setScores(gameManager.players.map { it.score }.toIntArray())
    }
}