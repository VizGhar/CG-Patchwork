package com.codingame.game.gui

import com.codingame.game.Patch

val Patch.image get() = when {
    id >= 0 -> "patch$id.png"
    id == -20 -> "patch-1.png"
    id == -26 -> "patch-2.png"
    id == -32 -> "patch-3.png"
    id == -44 -> "patch-4.png"
    id == -50 -> "patch-5.png"
    else -> throw IllegalArgumentException()
}

val Patch.polygon get() = when(id) {
    0 -> listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 60 to 120, 60 to 60, 0 to 60)
    1 -> listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 120 to 120, 120 to 180, 0 to 180, 0 to 120, 60 to 120, 60 to 60, 0 to 60)
    2 -> listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 180, 120 to 180, 120 to 120, 60 to 120, 60 to 60, 0 to 60)
    3 -> listOf(0 to 0, 240 to 0, 240 to 60, 60 to 60, 60 to 120, 0 to 120)
    4 -> listOf(0 to 0, 60 to 0, 60 to 60, 240 to 60, 240 to 120, 60 to 120, 60 to 180, 0 to 180)
    5 -> listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 180, 60 to 180, 60 to 60, 0 to 60)
    6 -> listOf(120 to 0, 180 to 0, 180 to 60, 300 to 60, 300 to 120, 180 to 120, 180 to 180, 120 to 180, 120 to 120, 0 to 120, 0 to 60, 120 to 60)
    7 -> listOf(0 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 60 to 120, 60 to 60,0 to 60)
    8 -> listOf(60 to 0, 120 to 0, 120 to 60, 240 to 60, 240 to 120, 120 to 120, 120 to 180, 60 to 180, 60 to 120, 0 to 120, 0 to 60, 60 to 60)
    9 -> listOf(0 to 0, 240 to 0, 240 to 60, 0 to 60)
    10 -> listOf(0 to 0, 60 to 0, 60 to 60, 240 to 60, 240 to 180, 180 to 180, 180 to 120, 0 to 120)
    11 -> listOf(0 to 0, 180 to 0, 180 to 60, 120 to 60, 120 to 120, 0 to 120)
    12 -> listOf(0 to 0, 180 to 0, 180 to 60, 120 to 60, 120 to 180, 60 to 180, 60 to 60, 0 to 60)
    13 -> listOf(0 to 0, 240 to 0, 240 to 60, 120 to 60, 120 to 120, 60 to 120, 60 to 60, 0 to 60)
    14 -> listOf(0 to 0, 180 to 0, 180 to 120, 120 to 120, 120 to 60, 60 to 60, 60 to 120, 0 to 120)
    15 -> listOf(0 to 0, 240 to 0, 240 to 60, 120 to 60, 120 to 120, 0 to 120)
    16 -> listOf(0 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 120 to 120, 120 to 60, 0 to 60)
    17 -> listOf(0 to 0, 180 to 0, 180 to 60, 120 to 60, 120 to 120, 60 to 120, 60 to 60, 0 to 60)
    18 -> listOf(0 to 0, 180 to 0, 180 to 60, 0 to 60)
    19 -> listOf(0 to 0, 180 to 0, 180 to 60, 60 to 60, 60 to 120, 0 to 120)
    20 -> listOf(0 to 0, 60 to 0, 60 to 60, 120 to 60, 120 to 0, 180 to 0, 180 to 180, 120 to 180, 120 to 120, 60 to 120, 60 to 180, 0 to 180)
    21 -> listOf(60 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 180 to 120, 180 to 180, 60 to 180, 60 to 120, 0 to 120, 0 to 60, 60 to 60)
    22 -> listOf(0 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 60 to 120, 60 to 60, 0 to 60)
    23 -> listOf(60 to 0, 120 to 0, 120 to 60, 240 to 60, 240 to 120, 180 to 120, 180 to 180, 120 to 180, 120 to 120, 0 to 120, 0 to 60, 60 to 60)
    24 -> listOf(0 to 0, 300 to 0, 300 to 60, 0 to 60)
    25 -> listOf(0 to 0, 240 to 0, 240 to 120, 180 to 120, 180 to 60, 60 to 60, 60 to 120, 0 to 120)
    26 -> listOf(0 to 0, 120 to 0, 120 to 120, 0 to 120)
    27 -> listOf(60 to 0, 120 to 0, 120 to 60, 180 to 60, 180 to 120, 120 to 120, 120 to 180, 60 to 180, 60 to 120, 0 to 120, 0 to 60, 60 to 60)
    28 -> listOf(60 to 0, 180 to 0, 180 to 60, 240 to 60, 240 to 120, 0 to 120, 0 to 60, 60 to 60)
    29 -> listOf(120 to 0, 180 to 0, 180 to 120, 0 to 120, 0 to 60, 120 to 60)
    30 -> listOf(0 to 0, 120 to 0, 120 to 120, 60 to 120, 60 to 60, 0 to 60)
    31 -> listOf(0 to 0, 60 to 0, 60 to 60, 120 to 60, 120 to 120, 0 to 120)
    32 -> listOf(0 to 0, 120 to 0, 120 to 60, 0 to 60)
    else -> emptyList()
}
