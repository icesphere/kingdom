package com.kingdom.model

import java.util.LinkedList
import java.util.Queue

abstract class IncompleteCard protected constructor(val cardName: String, val game: OldGame) {

    var extraOldCardActions: Queue<OldCardAction> = LinkedList()
        protected set

    var isEndTurn: Boolean = false

    protected open var isAllActionsSet: Boolean = false

    abstract val isCompleted: Boolean

    init {
        game.incompleteCard = this
    }

    abstract fun setWaitingDialogs()

    abstract fun setPlayerActionCompleted(playerId: Int)

    @Synchronized
    fun actionFinished(player: OldPlayer) {
        if (!player.isShowCardAction) {
            setPlayerActionCompleted(player.userId)
            if (!extraOldCardActions.isEmpty()) {
                game.setPlayerCardAction(game.currentPlayer!!, extraOldCardActions.remove())
            }
        }
        setWaitingDialogs()
    }

    abstract fun allActionsSet()
}
