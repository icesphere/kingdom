package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class CardFromDiscardToHand(cardsInDiscard: List<Card>) : SelectCardsFromCardAction("Choose a card from your discard pile to put into your hand", cardsInDiscard, 1, false), SelectFromDiscardAction {

    override fun processAction(player: Player): Boolean {
        //in case cards in discard have changed since this action was added
        cardChoices = player.cardsInDiscardCopy

        if (!super.processAction(player)) {
            return false
        }

        return player.cardsInDiscard.isNotEmpty()
    }

    override fun onSelectionDone(player: Player) {
        val card = selectedCards.first()

        player.removeCardFromDiscard(card)
        player.addCardToHand(card)
    }
}