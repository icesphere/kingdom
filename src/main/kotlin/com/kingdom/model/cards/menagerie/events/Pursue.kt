package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Pursue : MenagerieEvent(NAME, 2), ChooseCardActionCard {

    init {
        addBuys = 1
        special = "Name a card. Reveal the top 4 cards from your deck. Put the matches back and discard the rest."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cardsToSelectFrom = player.game.allCardsCopy
        player.chooseCardAction(special, this, cardsToSelectFrom, false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addEventLogWithUsername("named ${card.cardNameWithBackgroundColor}")

        val cards = player.removeTopCardsOfDeck(4, true)
        val matches = cards.filter { it.name == card.name }
        player.addCardsToTopOfDeck(matches)
        val cardsToDiscard = cards - matches
        player.addCardsToDiscard(cardsToDiscard)
    }

    companion object {
        const val NAME: String = "Pursue"
    }
}