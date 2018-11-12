package com.kingdom.model.cards.darkages.ruins

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.darkages.DarkAgesCard
import com.kingdom.model.players.Player

class Survivors : DarkAgesCard(NAME, CardType.ActionRuins, 0), ChoiceActionCard {

    init {
        special = "Look at the top 2 cards of your deck. Discard them or put them back in any order."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.revealTopCardsOfDeck(2)
        if (cards.isNotEmpty()) {
            player.makeChoice(this, Choice(1, "Discard"), Choice(2, "Put back on top of deck in any order"))
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val cards = player.removeTopCardsOfDeck(2)
            player.addCardsToDiscard(cards)
        } else {
            if (player.deck.size > 1) {
                val cards = player.removeTopCardsOfDeck(2)
                player.putCardsOnTopOfDeckInAnyOrder(cards)
            }
        }
    }

    companion object {
        const val NAME: String = "Survivors"
    }
}

