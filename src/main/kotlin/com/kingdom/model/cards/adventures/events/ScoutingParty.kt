package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class ScoutingParty : AdventuresEvent(NAME, 2), ChooseCardsActionCard {

    init {
        addBuys = 1
        special = "Look at the top 5 cards of your deck. Discard 3 and put the rest back in any order."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(5)
        if (cards.isEmpty()) {
            return
        }

        if (cards.size <= 3) {
            player.addCardsToDiscard(cards)
            player.addEventLogWithUsername("Discarded ${cards.groupedString} from the top of their deck")
        } else {

        }
        player.chooseCardsAction(3, "Discard 3 cards", this, cards, false, cards)
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        @Suppress("UNCHECKED_CAST")
        val topDeckCards = info as List<Card>

        val cardsToPutBackOnDeck = topDeckCards - cards

        player.addCardsToDiscard(cards, true)

        if (cardsToPutBackOnDeck.size == 1) {
            player.addCardToTopOfDeck(cardsToPutBackOnDeck.first())
        } else {
            player.putCardsOnTopOfDeckInAnyOrder(cardsToPutBackOnDeck)
        }
    }

    companion object {
        const val NAME: String = "Scouting Party"
    }
}