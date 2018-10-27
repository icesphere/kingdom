package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardGainedListenerForCardsInPlay
import com.kingdom.model.players.Player

class RoyalSeal : ProsperityCard(NAME, CardType.Treasure, 5), CardGainedListenerForCardsInPlay, ChoiceActionCard {

    var gainedCard: Card? = null

    init {
        testing = true
        isPlayTreasureCardsRequired = true
        addCoins = 2
        special = "While this is in play, when you gain a card, you may put that card onto your deck."
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        gainedCard = card

        player.yesNoChoice(this, "Put ${card.cardNameWithBackgroundColor} on top of your deck?")

        return true
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            player.isNextCardToTopOfDeck = true
        }

        player.cardAcquired(gainedCard!!)
    }

    companion object {
        const val NAME: String = "Royal Seal"
    }
}

