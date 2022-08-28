package com.codingame.game

import org.junit.Test
import kotlin.test.assertEquals

class TilesTest {

    @Test
    fun testRotations() {
        var tile = tiles[0].shape
        assertEquals("OO.|.OO", tile.string())
        tile = tile.rightRotated
        assertEquals(".O|OO|O.", tile.string())
        tile = tile.rightRotated
        assertEquals("OO.|.OO", tile.string())
        tile = tile.rightRotated
        assertEquals(".O|OO|O.", tile.string())

        tile = tiles[0].shape.mirrored
        assertEquals(".OO|OO.", tile.string())
        tile = tile.rightRotated
        assertEquals("O.|OO|.O", tile.string())
        tile = tile.rightRotated
        assertEquals(".OO|OO.", tile.string())
        tile = tile.rightRotated
        assertEquals("O.|OO|.O", tile.string())
    }
}