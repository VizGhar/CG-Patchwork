package com.codingame.game

import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule

data class Animation(val duration: Int, val action: (Double) -> Unit)

fun List<Animation>.run(g: GraphicEntityModule, gameManager: MultiplayerGameManager<Player>) {
    val turnDuration = sumOf { it.duration }
    gameManager.frameDuration = turnDuration
    var spent = 0.0
    forEach {
        val slice = it.duration.toDouble() / turnDuration
        it.action(spent -0.0001)
        spent += slice
        g.commitWorldState(minOf(1.0, spent))
    }
}