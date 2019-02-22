package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class CardFromDiscardToTopOfDeck(cardsInDiscard: List<Card>, private val maxCost: Int?) : SelectCardsFromCardAction("", cardsInDiscard, 1, true), SelectFromDiscardAction {

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
        return super.isCardActionable(card, cardLocation, player) && (maxCost == null || (card.debtCost == 0 && player.getCardCostWithModifiers(card) <= maxCost))
    }

    override fun processAction(player: Player): Boolean {
        //in case cards in discard have changed since this action was added
        cardChoices = player.cardsInDiscardCopy

        if (!super.processAction(player)) {
            return false
        }

        return if (maxCost == null) {
            player.cardsInDiscard.isNotEmpty()
        } else {
            player.cardsInDiscard.stream().anyMatch { c -> player.getCardCostWithModifiers(c) <= maxCost && c.debtCost == 0 }
        }
    }

    override fun onSelectionDone(player: Player) {
        val card = selectedCards.first()

        player.removeCardFromDiscard(card)
        player.addCardToTopOfDeck(card)
    }
}