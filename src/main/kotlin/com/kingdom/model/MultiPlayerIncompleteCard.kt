package com.kingdom.model

import com.kingdom.util.cardaction.NextActionHandler

import java.util.Collections
import java.util.HashMap

class MultiPlayerIncompleteCard : IncompleteCard {

    private val completedPlayers = Collections.synchronizedMap(HashMap<Int, Boolean>())
    private var currentPlayerHasAction: Boolean = false

    override val isCompleted: Boolean
        get() {
            var allCompleted = true
            synchronized(completedPlayers) {
                for (completed in completedPlayers.values) {
                    if (!completed) {
                        allCompleted = false
                        break
                    }
                }
            }
            return allCompleted
        }

    override var isAllActionsSet: Boolean = true

    constructor(cardName: String, game: OldGame, currentPlayerHasAction: Boolean) : super(cardName, game) {
        this.currentPlayerHasAction = currentPlayerHasAction
        for (player in game.playerMap.values) {
            var markCompleted = false
            if (!currentPlayerHasAction && player.userId == game.currentPlayerId) {
                markCompleted = true
            }
            completedPlayers[player.userId] = markCompleted
        }
    }

    constructor(cardName: String, game: OldGame, userId: Int) : super(cardName, game) {
        this.currentPlayerHasAction = false
        for (player in game.playerMap.values) {
            var markCompleted = true
            if (player.userId == userId) {
                markCompleted = false
            }
            completedPlayers[player.userId] = markCompleted
        }
    }

    override fun setPlayerActionCompleted(playerId: Int) {
        completedPlayers[playerId] = true
    }

    override fun allActionsSet() {
        isAllActionsSet = true
    }

    override fun setWaitingDialogs() {
        if (!game.currentPlayer!!.isShowCardAction && !game.playersWithCardActions.isEmpty()) {
            game.setPlayerCardAction(game.currentPlayer!!, OldCardAction.waitingForPlayersOldCardAction)
            return
        }
        if (isCompleted) {
            var loopIterations = 0
            while (game.hasNextAction() && game.hasIncompleteCard() && game.incompleteCard!!.isCompleted) {
                closeWaitingDialogs()
                game.removeIncompleteCard()
                NextActionHandler.handleAction(game, cardName)
                loopIterations++
                if (loopIterations > 5) {
                    val error = GameError(GameError.GAME_ERROR, "setWaitingDialogs-nextAction in never ending loop. Next action: " + game.nextAction)
                    game.logError(error, false)
                    game.removeNextAction()
                    game.removeIncompleteCard()
                    break
                }
            }
            if (!game.hasIncompleteCard() || game.incompleteCard!!.isCompleted) {
                if (game.playersWithCardActions.isEmpty()) {
                    closeWaitingDialogs()
                }
                if (!game.currentPlayer!!.isShowCardAction) {
                    game.removeIncompleteCard()
                }
            }
        } else if (isAllActionsSet) {
            if (currentPlayerHasAction) {
                for (player in game.players) {
                    if (!player.isShowCardAction) {
                        game.setPlayerCardAction(player, OldCardAction.waitingForPlayersOldCardAction)
                    }
                }
            } else {
                if (!game.currentPlayer!!.isShowCardAction) {
                    game.setPlayerCardAction(game.currentPlayer!!, OldCardAction.waitingForPlayersOldCardAction)
                }
            }
        }
    }

    private fun closeWaitingDialogs() {
        for (player in game.players) {
            if (player.isShowCardAction && player.oldCardAction!!.isWaitingForPlayers) {
                game.closeCardActionDialog(player)
                game.closeLoadingDialog(player)
            }
        }
    }
}
