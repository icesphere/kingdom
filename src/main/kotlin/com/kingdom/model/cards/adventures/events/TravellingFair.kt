package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardGainedListenerForEventsBought
import com.kingdom.model.players.Player

class TravellingFair : AdventuresEvent(NAME, 2), CardGainedListenerForEventsBought, ChoiceActionCard {

    var ignoreNextCardGained = false

    init {
        addBuys = 2
        special = "When you gain a card this turn, you may put it onto your deck."
        fontSize = 10
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        if (ignoreNextCardGained) {
            ignoreNextCardGained = false
            return false
        }

        if (player.isNextCardToTopOfDeck) {
            return false
        }

        player.yesNoChoice(this, "Put ${card.cardNameWithBackgroundColor} on top of your deck?", card)

        return true
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.isNextCardToTopOfDeck = true
        } else {
            ignoreNextCardGained = true
        }

        player.cardGained(info as Card)
    }

    companion object {
        const val NAME: String = "Travelling Fair"
    }
}