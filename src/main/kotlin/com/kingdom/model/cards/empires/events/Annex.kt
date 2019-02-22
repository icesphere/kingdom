package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.cards.supply.Duchy
import com.kingdom.model.players.Player

class Annex : EmpiresEvent(NAME, 0, 8), ChooseCardsActionCard {

    init {
        special = "Look through your discard pile. Shuffle all but up to 5 cards from it into your deck. Gain a Duchy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.cardsInDiscard.isNotEmpty()) {
            val numCardsToChoose = minOf(player.cardsInDiscard.size, 5)
            player.chooseCardsAction(numCardsToChoose, "Choose up to $numCardsToChoose to leave in your discard pile (the unselected cards will be shuffled into your deck)", this, player.cardsInDiscardCopy, true)
        } else {
            player.showInfoMessage("Your discard pile is empty")
            player.gainSupplyCard(Duchy(), true)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        player.addEventLogWithUsername("shuffled all but ${cards.size} cards from their discard pile into their deck")

        player.removeCardsFromDiscard(cards)

        player.shuffleDiscardIntoDeck()

        player.addCardsToDiscard(cards)

        player.gainSupplyCard(Duchy(), true)
    }

    companion object {
        const val NAME: String = "Annex"
    }
}