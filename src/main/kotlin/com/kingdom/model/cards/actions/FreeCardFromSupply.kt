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
        return true
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!

        player.game.removeCardFromSupply(card)

        player.addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} from the supply")

        when (destination) {
            CardLocation.Hand -> player.gainCardToHand(card)
            CardLocation.Deck -> player.gainCardToTopOfDeck(card)
            else -> player.cardGained(card)
        }

        return true
    }
}