package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

open class FreeCardFromSupply(private val maxCost: Int?, text: String, private val cardActionableExpression: ((card: Card) -> Boolean)?, private val destination: CardLocation = CardLocation.Discard) : Action(text) {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return ((cardLocation == CardLocation.Supply)
                && (maxCost == null || player.getCardCostWithModifiers(card) <= maxCost))
                && (cardActionableExpression == null || cardActionableExpression.invoke(card))
    }

    override fun processAction(player: Player): Boolean {
        player.game.addHistory(player.username + " is choosing a free card from the supply")
        return true
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!

        player.game.removeCardFromSupply(card)

        player.addGameLog(player.username + " acquired a free card from the supply: " + card.cardNameWithBackgroundColor)

        when (destination) {
            CardLocation.Hand -> player.acquireCardToHand(card)
            CardLocation.Deck -> player.acquireCardToTopOfDeck(card)
            else -> player.cardAcquired(card)
        }

        return true
    }
}