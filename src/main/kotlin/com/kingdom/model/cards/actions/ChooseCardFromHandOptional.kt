package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class ChooseCardFromHandOptional(private val chooseCardActionCard: ChooseCardActionCardOptional, text: String, private val cardActionableExpression: ((card: Card) -> Boolean)?) : Action(text) {

    override var isShowDoNotUse: Boolean = true

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Hand && (cardActionableExpression == null || cardActionableExpression.invoke(card))
    }

    override fun processAction(player: Player): Boolean {
        return player.hand.any { cardActionableExpression == null || cardActionableExpression.invoke(it) }
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        chooseCardActionCard.onCardChosen(player, result.selectedCard)
        return true
    }

    override fun onNotUsed(player: Player) {
        chooseCardActionCard.onCardChosen(player, null, null)
    }
}