package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.TurnEndedListenerForEventsBought
import com.kingdom.model.players.Player

class Donate : EmpiresEvent(NAME, 0, 8), TurnEndedListenerForEventsBought, TrashCardsForBenefitActionCard {

    init {
        special = "After this turn, put all cards from your deck and discard pile into your hand, trash any number, shuffle your hand into your deck, then draw 5 cards."
    }

    override fun onTurnEnded(player: Player) {
        player.addCardsToHand(player.cardsInDiscard + player.deck)
        player.clearDiscard()
        player.deck.clear()
        player.optionallyTrashCardsFromHandForBenefit(this, player.hand.size, "Trash any number of cards and then your hand will be shuffled into your deck and you will draw 5 cards")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        player.shuffleHandIntoDeck()
    }

    companion object {
        const val NAME: String = "Donate"
    }
}