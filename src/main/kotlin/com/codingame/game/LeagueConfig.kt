package com.codingame.game

lateinit var league: League

enum class League(
    val patchTurns: List<Int>,
    val earnTurns: List<Int>,
    val scoreBonusMultiplier: Int,
    val scoreFilledMultiplier: Int,
    val scoreMinusPointsMultiplier: Int,
    val scoreMoneyMultiplier: Int,
    val skipMultiplier: Int,
    val rotationsAllowed: Boolean,
    val gameDuration: Int,
    private val defaultInitialButtons: Int
    ) {

    /**
     * No earning and no special patches can be acquired in league 1.
     * No bonus button is present. Only covered fields will affect the score - 1 point for each covered square
     * Rotations/flipping is disabled
     */
    L1(
        patchTurns = listOf(),
        earnTurns = listOf(),
        scoreBonusMultiplier = 0,
        scoreFilledMultiplier = 1,
        scoreMinusPointsMultiplier = 0,
        scoreMoneyMultiplier = 0,
        skipMultiplier = 0,
        rotationsAllowed = false,
        gameDuration = 19,
        defaultInitialButtons = 200
    ),

    /**
     * Now earning is turned on. But neither bonus button not special patches are available.
     * Scoring works normal
     *
     * + rotations
     * + money
     */
    L2(
        patchTurns = listOf(),
        earnTurns = listOf(5, 11, 17, 23, 29, 35, 41, 47, 53),
        scoreBonusMultiplier = 0,
        scoreFilledMultiplier = 0,
        scoreMinusPointsMultiplier = -2,
        skipMultiplier = 1,
        scoreMoneyMultiplier = 1,
        rotationsAllowed = true,
        gameDuration = 53,
        defaultInitialButtons = 5,
    ),

    /**
     * Fully featured game
     *
     * + bonuses
     */
    FULL(
        patchTurns = listOf(20, 26, 32, 44, 50),
        earnTurns = listOf(5, 11, 17, 23, 29, 35, 41, 47, 53),
        scoreBonusMultiplier = 7,
        scoreFilledMultiplier = 0,
        scoreMinusPointsMultiplier = -2,
        skipMultiplier = 1,
        scoreMoneyMultiplier = 1,
        rotationsAllowed = true,
        gameDuration = 53,
        defaultInitialButtons = 5,
    );

    val tileEarning get() = earnTurns.isNotEmpty()
    val specialPatchesEnabled get() = patchTurns.isNotEmpty()
    val initialButtons get() = INITIAL_MONEY

    companion object {
        fun leagueInit(level: Int) {
            league = when (level) {
                1 -> L1
                2 -> L2
                3, 4 -> FULL
                else -> throw IllegalStateException("Only leagues 1-4 supported")
            }
            INITIAL_MONEY = league.defaultInitialButtons
        }
    }
}
