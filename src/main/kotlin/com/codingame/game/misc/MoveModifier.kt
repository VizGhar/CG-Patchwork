package com.codingame.game.misc

import com.codingame.game.BoardManager
import com.codingame.game.Move
import com.codingame.game.TurnResult

/**
 * Players can play illogical moves or there are some new moves when reaching higher league. This class
 * helps adjust invalid moves if it makes sense.
 */
class MoveModifier(private val boardManager: BoardManager) {

    fun fixCommand(command: Move, onInvalidPlay: (TurnResult) -> Unit) : Move {
        return when(command) {
            is Move.Play -> fixPlayCommand(command, onInvalidPlay)
            is Move.Skip -> fixSkipCommand(onInvalidPlay)
            is Move.Unknown -> Move.Unknown
        }
    }

    /**
     * If there is no SKIP available (special patch is in play), change it to PLAY command and place
     * the patch to first available position. This is just to make sure user won't immediately crash
     * after reaching league with special patches.
     */
    private fun fixSkipCommand(onInvalidPlay: (TurnResult) -> Unit) : Move {
        val activePlayerId = boardManager.actualPlayerId
        if (boardManager.players[activePlayerId].availablePatches == 0) {
            return Move.Skip
        } else {
            onInvalidPlay(TurnResult.CantSkip)
            for (y in 0 until 9) {
                for (x in 0 until 9) {
                    if (!boardManager.players[boardManager.actualPlayerId].board[y][x]) {
                        return Move.Play(boardManager.gameBonusPatches[0].id, x, y, false, 0)
                    }
                }
            }
            throw IllegalStateException("There is no space to place special patch, shouldn't happen in game")
        }
    }

    private fun fixPlayCommand(command: Move.Play, onInvalidPlay: (TurnResult) -> Unit) : Move {
        return when(val result = boardManager.playPatch(command.patchId, command.rightRotations, command.flip, command.x, command.y, false)) {
            TurnResult.InvalidPatchId, TurnResult.InvalidPatchPlacement, TurnResult.NoMoney -> {
                onInvalidPlay(result)
                fixSkipCommand(onInvalidPlay)
            }

            TurnResult.UnknownCommand -> {
                onInvalidPlay(result)
                command
            }

            else -> { command }
        }
    }
}