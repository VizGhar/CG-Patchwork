package com.codingame.game.misc

import com.codingame.game.Move

class GameLog {

    private var lastActivePlayerId = -1
    private val gameLogPlayer0 = mutableListOf<Move>()
    private val gameLogPlayer1 = mutableListOf<Move>()

    fun getLog(playerId: Int) : List<Move> {
        val opponentLog = when(playerId) {
            0 -> gameLogPlayer1
            else -> gameLogPlayer0
        }

        val activePlayerLog = when(playerId) {
            0 -> gameLogPlayer0
            else -> gameLogPlayer1
        }

        if (playerId != lastActivePlayerId) {
            activePlayerLog.clear()
        }

        lastActivePlayerId = playerId

        val result = opponentLog.map { it }
        opponentLog.clear()
        return result
    }

    fun log(playerId: Int, move: Move) {
        when(playerId) {
            0 -> gameLogPlayer0
            else -> gameLogPlayer1
        }.add(move)
    }
}