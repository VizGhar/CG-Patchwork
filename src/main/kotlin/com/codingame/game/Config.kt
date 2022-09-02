package com.codingame.game

import java.util.*

var BONUS_BUTTON_SIZE = 7
var INITIAL_MONEY = 5

private fun getFromParams(params: Properties, name: String, defaultValue: Int): Int {
    val inputValue = params.getProperty(name)
    if (inputValue != null) {
        try {
            return inputValue.toInt()
        } catch (e: NumberFormatException) {
            // Do naught
        }
    }
    return defaultValue
}

fun take(params: Properties) {
    BONUS_BUTTON_SIZE = getFromParams(params, "BONUS_BUTTON_SIZE", BONUS_BUTTON_SIZE)
    INITIAL_MONEY = getFromParams(params, "INITIAL_MONEY", INITIAL_MONEY)
}
