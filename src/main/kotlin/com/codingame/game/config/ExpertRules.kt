package com.codingame.game.config

import com.codingame.game.Player
import com.codingame.gameengine.core.MultiplayerGameManager
import java.util.*

/**
 * Fow covering how big square on canvas the bonus is achieved. There are expansions to original board game, where
 * you can receive additional 5 points for 5x5 square.
 */
var BONUS_BUTTON_SIZE = 7

/**
 * How much money player receives on start of the game - can't overrite for league 1/2
 */
var INITIAL_MONEY = 5

class ExpertRules(private val gameManager: MultiplayerGameManager<Player>) {

    private fun getFromParams(params: Properties, name: String, defaultValue: Int): Int {
        val inputValue = params.getProperty(name)
        if (inputValue != null) {
            try {
                return inputValue.toInt()
            } catch (_: NumberFormatException) {
            }
        }
        return defaultValue
    }

    fun init() {
        val params = gameManager.gameParameters
        BONUS_BUTTON_SIZE = getFromParams(params, "BONUS_BUTTON_SIZE", BONUS_BUTTON_SIZE)
        INITIAL_MONEY = getFromParams(params, "INITIAL_MONEY", INITIAL_MONEY)
    }

}
