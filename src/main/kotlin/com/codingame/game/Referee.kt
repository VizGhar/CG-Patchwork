package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.endscreen.EndScreenModule
import com.google.inject.Inject
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

@Suppress("unused")
class Referee : AbstractReferee() {

    @Inject private lateinit var gameManager: MultiplayerGameManager<Player>
    @Inject private lateinit var endScreenModule: EndScreenModule
    @Inject private lateinit var gui: Interface

    private val random by lazy { SecureRandom(gameManager.seed.toString().toByteArray()).asKotlinRandom() }
    private val gameTiles by lazy { tiles.dropLast(1).shuffled(random) + tiles.last() }
    private val boardManager by lazy { BoardManager(gameTiles, gui) }

    override fun init() {
        // Initialize your game here.
        gameManager.firstTurnMaxTime = 1000

        gui.hud(gameManager.players[0], gameManager.players[1])
        gui.showTilesBelt(boardManager.orderedGameTiles, boardManager.gameBonusTiles)

        gameManager.turnMaxTime = 50
        gameManager.maxTurns = 200
        gameManager.frameDuration = 600
    }

    override fun gameTurn(turn: Int) {
        gui.showTilesBelt(boardManager.orderedGameTiles, boardManager.gameBonusTiles)
        val activePlayerId = boardManager.actualPlayerId
        val activePlayer = gameManager.players[activePlayerId]

        // player's first turn
        if (turn <= gameManager.playerCount) {
            activePlayer.sendInputLine(EARNING_TURNS.size.toString())
            activePlayer.sendInputLine(EARNING_TURNS.joinToString(" "))
            activePlayer.sendInputLine(PATCH_TURNS.size.toString())
            activePlayer.sendInputLine(PATCH_TURNS.joinToString(" "))
            activePlayer.sendInputLine(gameTiles.size.toString())
            for (tile in gameTiles) {
                activePlayer.sendInputLine(tile.toString())
            }
        }

        // every turn (including first one)
        activePlayer.sendInputLine("${boardManager.players[activePlayerId].money} ${boardManager.players[activePlayerId].position}")
        activePlayer.sendInputLine("${boardManager.players[(activePlayerId + 1) % 2].money} ${boardManager.players[(activePlayerId + 1) % 2].position}")

        // available tiles specification
        if (boardManager.players[activePlayerId].availablePatches == 0) {
            val playerTiles = boardManager.availableTiles
            activePlayer.sendInputLine(playerTiles.size.toString())
            for (tile in playerTiles) {
                activePlayer.sendInputLine(tile.toString())
            }
        } else {
            activePlayer.sendInputLine("1")
            activePlayer.sendInputLine(boardManager.gameBonusTiles[0].toString())
        }

        activePlayer.execute()

        try {
            val outputs = activePlayer.outputs
            if (outputs.size != 1) { activePlayer.deactivate(String.format("%d Single line of input expected", activePlayer.index)); return }

            val output = outputs[0].split(" ")
            val result = when {
                output[0] == "SKIP" -> boardManager.skip()
                output[0] == "TAKE" && output.drop(1).take(5).none { it.toIntOrNull() == null } -> boardManager.move(
                    tileid = output[1].toInt(),
                    orientation = output[2].toInt(),
                    mirrored = output[3].toInt() == 1,
                    x = output[4].toInt(),
                    y = output[5].toInt())
                else -> TurnResult.UNKNOWN_COMMAND
            }

            when(result){
                TurnResult.UNKNOWN_COMMAND -> activePlayer.deactivate(String.format("$%d unknown command", activePlayer.index))
                TurnResult.INVALID_TILE_ID -> activePlayer.deactivate(String.format("$%d cannot pick this tile", activePlayer.index))
                TurnResult.INVALID_TILE_PLACEMENT -> activePlayer.deactivate(String.format("$%d cannot place tile on that position", activePlayer.index))
                TurnResult.NO_MONEY -> activePlayer.deactivate(String.format("$%d cannot afford to buy required tile", activePlayer.index))
                TurnResult.CANT_SKIP -> activePlayer.deactivate(String.format("$%d cannot SKIP placement of BONUS PATCH - see rules", activePlayer.index))
                TurnResult.OK -> {}
            }

            gui.updateMoney(boardManager.players[0].money, boardManager.players[1].money)

            if (boardManager.players.any { it.position < TOTAL_TURNS }) {
                return
            }

            boardManager.score().forEachIndexed { index, score -> gameManager.players[index].score = score }

        } catch (e: AbstractPlayer.TimeoutException) {
            activePlayer.deactivate(String.format("$%d timeout!", activePlayer.index))
        }
        gameManager.endGame()
    }

    override fun onEnd() {
        endScreenModule.setScores(gameManager.players.map { it.score }.toIntArray())
    }
}