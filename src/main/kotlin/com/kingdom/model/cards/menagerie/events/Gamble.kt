package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Gamble : MenagerieEvent(NAME, 2), ChoiceActionCard {

    init {
        addBuys = 1
        special = "Reveal the top card of your deck. If it’s a Treasure or Action, you may play it. Otherwise, discard it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val topCard = player.removeTopCardOfDeck()
        if (topCard != null) {
            if (topCard.isTreasure || topCard.isAction) {
                player.yesNoChoice(this, "Play ${topCard.cardNameWithBackgroundColor}?", topCard)
            } else {
                player.addCardToDiscard(topCard, true, true)
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val topCard = info as Card

        if (choice == 1) {
            player.addActions(1)
            player.playCard(topCard)
        } else {
            player.addCardToDiscard(topCard, true, true)
        }
    }

    companion object {
        const val NAME: String = "Gamble"
    }
}