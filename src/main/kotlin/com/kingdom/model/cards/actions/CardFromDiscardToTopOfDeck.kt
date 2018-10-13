package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class CardFromDiscardToTopOfDeck(cardsInDiscard: List<Card>, private val maxCost: Int?) : SelectCardsAction("", cardsInDiscard, 1, true), SelectFromDiscardAction {

    override var isShowDoNotUse: Boolean = true

    override val isShowDone: Boolean = false

    init {
        text = "Choose a card from your discard pile "
        if (maxCost != null) {
            text += "of cost $maxCost or less "
        }
        text += "to put on top of your deck"
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Discard && (maxCost == null || player.getCardCostWithModifiers(card) <= maxCost)
    }

    override fun processAction(player: Player): Boolean {
        return if (maxCost == null) {
            player.discard.isNotEmpty()
        } else {
            player.discard.stream().anyMatch { c -> player.getCardCostWithModifiers(c) <= maxCost }
        }
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard
        player.discard.remove(card)
        player.addCardToTopOfDeck(card!!)
        return true
    }
}