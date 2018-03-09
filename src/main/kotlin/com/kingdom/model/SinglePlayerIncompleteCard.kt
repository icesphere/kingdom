package com.kingdom.model

import com.kingdom.util.cardaction.NextActionHandler

class SinglePlayerIncompleteCard(cardName: String, game: OldGame) : IncompleteCard(cardName, game) {

    override var isCompleted: Boolean = false
        private set

    override fun setPlayerActionCompleted(playerId: Int) {
        isCompleted = true
    }

    override fun allActionsSet() {
        //not used for single player incomplete card
    }

    override fun setWaitingDialogs() {
        if (!game.currentPlayer!!.isShowCardAction && !game.playersWithCardActions.isEmpty()) {
            game.setPlayerCardAction(game.currentPlayer!!, OldCardAction.waitingForPlayersOldCardAction)
            return
        }
        if (isCompleted && extraOldCardActions.isEmpty() && !game.currentPlayer!!.isShowCardAction) {
            while (game.hasNextAction()) {
                NextActionHandler.handleAction(game, cardName)
            }
            if (game.hasIncompleteCard() && game.incompleteCard!!.isCompleted) {
                game.removeIncompleteCard()
            }
        }
    }
}
