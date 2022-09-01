package com.codingame.game

import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule

data class Animation(val duration: Int, val action: () -> Unit)

fun List<Animation>.run(g: GraphicEntityModule, gameManager: MultiplayerGameManager<Player>) {
    val turnDuration = sumOf { it.duration }
    gameManager.frameDuration = turnDuration
    var spent = 0.0
    forEach {
        val slice = it.duration.toDouble() / turnDuration
        spent += slice
        it.action()
        g.commitWorldState(spent)
    }
}