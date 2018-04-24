package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class FreeCardFromSupply(private val maxCost: Int?, text: String, private val destination: CardLocation = CardLocation.Discard, val cardType: CardType? = null) : Action(text) {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return ((cardLocation == CardLocation.Supply)
                && (maxCost == null || player.getCardCostWithModifiers(card) <= maxCost))
                && (cardType == null || card.type == cardType)
    }

    override fun processAction(player: Player): Boolean {
        player.game.addHistory(player.username + " is choosing a free card from the supply")
        return true
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!

        player.game.removeCardFromSupply(card)

        player.addGameLog(player.username + " acquired a free card from the supply: " + card.cardNameWithBackgroundColor)

        if (destination == CardLocation.Hand) {
            player.acquireCardToHand(card)
        } else if (destination == CardLocation.Deck) {
            player.acquireCardToTopOfDeck(card)
        } else {
            player.cardAcquired(card)
        }

        return true
    }
}