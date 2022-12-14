package com.codingame.game

import com.codingame.game.config.ExpertRules
import com.codingame.game.config.League
import com.codingame.game.config.league
import com.codingame.game.gui.Animator
import com.codingame.game.gui.Interface
import com.codingame.game.misc.GameLog
import com.codingame.game.misc.MoveModifier
import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.endscreen.EndScreenModule
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.toggle.ToggleModule
import com.codingame.gameengine.module.tooltip.TooltipModule
import com.google.inject.Inject
import view.modules.InteractiveDisplayModule

sealed class Move {
    object Skip : Move()
    data class Play(val patchId: Int, val x: Int, val y: Int, val flip: Boolean, val rightRotations: Int): Move()
    object Unknown: Move()
}

fun Move.string() = when {
    this is Move.Play && league.rotationsAllowed -> "PLAY $patchId $x $y ${if (flip) "1" else "0"} $rightRotations"
    this is Move.Play -> "PLAY $patchId $x $y"
    this is Move.Skip -> "SKIP"
    else -> ""
}

@Suppress("unused")
class Referee : AbstractReferee() {

    @Inject private lateinit var graphicsModule: GraphicEntityModule
    @Inject private lateinit var gameManager: MultiplayerGameManager<Player>
    @Inject private lateinit var endScreenModule: EndScreenModule
    @Inject private lateinit var toggleModule: ToggleModule
    @Inject private lateinit var tooltipModule: TooltipModule
    @Inject private lateinit var interactiveDisplayModule: InteractiveDisplayModule
    private val gameLog by lazy { GameLog() }
    private val boardManager by lazy { BoardManager(gameManager) }
    private val animator by lazy { Animator(graphicsModule, gameManager, gui, boardManager) }
    private val expertRules by lazy { ExpertRules(gameManager) }
    private val gui by lazy { Interface(toggleModule, tooltipModule, graphicsModule, interactiveDisplayModule, boardManager, gameManager) }
    private val moveModifier by lazy { MoveModifier(boardManager) }

    override fun init() {
        League.leagueInit(gameManager.leagueLevel)  // init game
        expertRules.init()                          // override game settings with expert mode rules
        gui.init()

        gameManager.firstTurnMaxTime = 1000
        gameManager.turnMaxTime = 100
        gameManager.maxTurns = 80
        gameManager.frameDuration = 600
    }

    private fun input(turn: Int) {
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
        for (i in 0..8) { activePlayer.sendInputLine(boardMe.board[i].joinToString("") { if (it) "O" else "." }) }

        activePlayer.sendInputLine("${boardOpponent.money} ${boardOpponent.position} ${boardOpponent.playedPatches.sumOf { it.earn }}")
        for (i in 0..8) { activePlayer.sendInputLine(boardOpponent.board[i].joinToString("") { if (it) "O" else "." }) }

        activePlayer.sendInputLine(boardManager.remainingPatches.size.toString())
        for (patch in boardManager.remainingPatches) { activePlayer.sendInputLine(patch.toString()) }

        if (boardManager.players[activePlayerId].availablePatches != 0) {
            activePlayer.sendInputLine(boardManager.gameBonusPatches[0].id.toString())
        } else {
            activePlayer.sendInputLine("0")
        }

        val opponentLog = gameLog.getLog(activePlayerId)
        activePlayer.sendInputLine("${opponentLog.size}")
        for (log in opponentLog) { activePlayer.sendInputLine(log.string()) }

        activePlayer.execute()
    }

    data class Output(val move: Move, val message: String)

    private fun parseOutput(output: List<String>): Output {
        val message: String
        val move: Move
        when {
            output[0] == "SKIP" -> {
                message = output.drop(1).joinToString(" ")
                move = Move.Skip
            }
            output[0] == "PLAY" -> {
                val patchId = output.getOrNull(1)?.toIntOrNull()
                val x = output.getOrNull(2)?.toIntOrNull()
                val y = output.getOrNull(3)?.toIntOrNull()
                val flip = if (!league.rotationsAllowed) null else output.getOrNull(4)?.toIntOrNull()
                val rightRotations = if (!league.rotationsAllowed) null else output.getOrNull(5)?.toIntOrNull()

                if (patchId == null || x == null || y == null) {
                    message = ""
                    move = Move.Unknown
                } else {
                    message = if (flip == null) {
                        output.drop(4).joinToString(" ")
                    } else if (rightRotations == null) {
                        output.drop(5).joinToString(" ")
                    } else {
                        output.drop(6).joinToString(" ")
                    }

                    move = Move.Play(
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
                move = Move.Unknown
            }
        }
        return Output(move, message)
    }

    override fun gameTurn(turn: Int) {
        val activePlayerId = boardManager.actualPlayerId
        val activePlayer = gameManager.players[activePlayerId]

        input(turn)

        try {
            val outputs = activePlayer.outputs

            if (outputs.size != 1) { activePlayer.deactivate(String.format("%d Single line of input expected", activePlayer.index)); return }

            val (requiredMove, message) = parseOutput(outputs[0].split(" "))

            val adjustedMove = moveModifier.fixCommand(requiredMove) {
                when(it){
                    TurnResult.CantSkip -> gameManager.addTooltip(activePlayer, "${activePlayer.nicknameToken} cannot skip while holding special patch - calling PLAY instead")
                    TurnResult.InvalidPatchId -> gameManager.addTooltip(activePlayer, "${activePlayer.nicknameToken} cannot pick this patch - calling SKIP instead")
                    TurnResult.InvalidPatchPlacement -> gameManager.addTooltip(activePlayer, "${activePlayer.nicknameToken} cannot place patch there - calling SKIP instead")
                    TurnResult.NoMoney -> gameManager.addTooltip(activePlayer, "${activePlayer.nicknameToken} cannot afford to buy required patch - calling SKIP instead")
                    else -> {}
                }
            }

            val moveResult = getMoveResult(adjustedMove)

            if (moveResult !is TurnResult.OK) {
                activePlayer.deactivate(String.format("%s unknown command", activePlayer.nicknameToken))
                boardManager.computeScore().forEachIndexed { index, score -> gameManager.players[index].score = score }
                activePlayer.score = -1
                gameManager.endGame()
                return
            }

            gameLog.log(activePlayerId, adjustedMove)

            // Run animations
            animator.animateGameState(activePlayerId, adjustedMove, moveResult, message)

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