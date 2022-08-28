package com.codingame.game

import org.junit.Test
import kotlin.test.assertEquals

class BoardManagerTest {

    @Test
    fun getActualPlayerId() {
        val boardManager = BoardManager(tiles.asList(), Interface())
        assertEquals(0, boardManager.actualPlayerPosition)
        assertEquals(0, boardManager.actualPlayerId)
        assertEquals(53, boardManager.remainingTurns)
        val moveResult1 = boardManager.move(0, 0, false, 0, 0)
        assertEquals(TurnResult.OK, moveResult1)

        assertEquals(0, boardManager.actualPlayerPosition)
        assertEquals(1, boardManager.actualPlayerId)
        assertEquals(53, boardManager.remainingTurns)
        val moveResult2 = boardManager.move(0, 0, false, 0, 0)
        assertEquals(TurnResult.OK, moveResult2)

        assertEquals(1, boardManager.actualPlayerId)
        assertEquals(53 - 6, boardManager.remainingTurns)
        val moveResult3 = boardManager.move(0, 0, false, 0, 0)
        assertEquals(TurnResult.INVALID_TILE_PLACEMENT, moveResult3)
        assertEquals(1, boardManager.actualPlayerId)

        val moveResult4 = boardManager.move(0, 0, false, 2, 0)
        assertEquals(TurnResult.OK, moveResult4)
        assertEquals(0, boardManager.actualPlayerId)

    }

}