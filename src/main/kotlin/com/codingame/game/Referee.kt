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
        leagueInit(gameManager.leagueLevel)
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
            activePlayer.sendInputLine(boardManager.remainingPatches.size.toString())
            for (tile in boardManager.remainingPatches) {
                activePlayer.sendInputLine(tile.toString())
            }
        }

        // every turn (including first one)
        activePlayer.sendInputLine("${boardMe.money} ${boardMe.position} ${boardMe.playedTiles.sumOf { it.earn }}")
        for (i in 0..8) {
            activePlayer.sendInputLine(boardMe.board[i].joinToString("") { if (it) "O" else "." })
        }

        activePlayer.sendInputLine("${boardOpponent.money} ${boardOpponent.position} ${boardOpponent.playedTiles.sumOf { it.earn }}")
        for (i in 0..8) {
            activePlayer.sendInputLine(boardOpponent.board[i].joinToString("") { if (it) "O" else "." })
        }

        // available tiles specification
        if (boardManager.players[activePlayerId].availablePatches == 0) {
            val playerTiles = boardManager.availablePatches
            activePlayer.sendInputLine(playerTiles.size.toString())
            for (tile in playerTiles) {
                activePlayer.sendInputLine(tile.toString())
            }
        } else {
            activePlayer.sendInputLine("1")
            activePlayer.sendInputLine(boardManager.gameBonusPatches[0].toString())
        }

        activePlayer.execute()

        try {
            val outputs = activePlayer.outputs
            if (outputs.size != 1) { activePlayer.deactivate(String.format("%d Single line of input expected", activePlayer.index)); return }

            val output = outputs[0].split(" ")
            val message: String
            val move = when {
                output[0] == "SKIP" -> {
                    message = output.drop(1).joinToString(" ")
                    Move.Skip
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

            val moveResult = when(move) {
                is Move.Play -> boardManager.playTile(move.patchId, move.rightRotations, move.flip, move.x, move.y)
                Move.Skip -> boardManager.playSkip()
                Move.Unknown -> TurnResult.UnknownCommand
            }

            when(moveResult){
                TurnResult.UnknownCommand -> activePlayer.deactivate(String.format("$%d unknown command", activePlayer.index))
                TurnResult.InvalidTileId -> activePlayer.deactivate(String.format("$%d cannot pick this tile", activePlayer.index))
                TurnResult.InvalidTilePlacement -> activePlayer.deactivate(String.format("$%d cannot place tile on that position${if (!league.rotationsAllowed) ". Be aware, you cannot do rotations/flips in this league." else ""}", activePlayer.index))
                TurnResult.NoMoney -> activePlayer.deactivate(String.format("$%d cannot afford to buy required tile", activePlayer.index))
                is TurnResult.OK -> {}
            }

            if (moveResult !is TurnResult.OK) {
                boardManager.computeScore().forEachIndexed { index, score -> gameManager.players[index].score = score }
                activePlayer.score = 0
                gameManager.endGame()
                return
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
                animations += (1..(tiles.firstOrNull { it.id == move.patchId }?.price ?: 0)).map {
                    listOf(
                        Animation(200) { from -> gui.pulseIn(from, activePlayerId) },
                        Animation(200) { from -> gui.pulseOut(from, activePlayerId) })
                }.flatten()

                // move patch to proper position
                animations += Animation(500) { from -> gui.move(from, activePlayerId, move.patchId, move.x, move.y, move.flip, move.rightRotations) }
            }

            // move time token
            animations += Animation(300) { from -> gui.updateTime(from, boardManager.players[0].position, boardManager.players[1].position) }

            if (moveResult.bonusAchieved) { animations += Animation(500) { from -> gui.acquireBonus(from, activePlayerId) } }
            animations += Animation(300) { from -> gui.updateMoney(from, boardManager.players[0].money, boardManager.players[1].money) }
            animations += Animation(1000) { from -> gui.showTilesBelt(from, boardManager.remainingPatches) }
            animations.run(g, gameManager)

            // End game
            if (boardManager.players.any { it.position < TOTAL_TURNS }) { return }
            boardManager.computeScore().forEachIndexed { index, score -> gameManager.players[index].score = score }
        } catch (e: AbstractPlayer.TimeoutException) {
            activePlayer.deactivate(String.format("$%d timeout!", activePlayer.index))
        }
        gameManager.endGame()
    }

    override fun onEnd() {
        endScreenModule.setScores(gameManager.players.map { it.score }.toIntArray())
    }
}