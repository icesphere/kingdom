package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class ChooseCardFromSupply(private val chooseCardActionCard: ChooseCardActionCard, text: String, private val cardActionableExpression: ((card: Card) -> Boolean)?, private val info: Any?) : Action(text) {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Supply && (cardActionableExpression == null || cardActionableExpression.invoke(card)) && player.game.isCardAvailableInSupply(card)
    }

    override fun processAction(player: Player): Boolean = true

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        chooseCardActionCard.onCardChosen(player, result.selectedCard!!, info)
        return true
    }
}