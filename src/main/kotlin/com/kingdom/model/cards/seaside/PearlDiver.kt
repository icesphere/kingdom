package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class PearlDiver : SeasideCard(NAME, CardType.Action, 2), ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        special = "Look at the bottom card of your deck. You may put it on top."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.deck.isEmpty()) {
            player.shuffleDiscardIntoDeck()
        }
        if (player.deck.isNotEmpty()) {
            val bottomDeckCard = player.deck.last()
            player.yesNoChoice(this, "Put ${bottomDeckCard.cardNameWithBackgroundColor} on top of your deck?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val bottomDeckCard = player.deck.removeAt(player.deck.lastIndex)
            player.addUsernameGameLog("added the bottom card of their deck to the top of their deck")
            player.addCardToTopOfDeck(bottomDeckCard, false)
        }
    }

    companion object {
        const val NAME: String = "Pearl Diver"
    }
}

