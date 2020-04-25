package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Hostelry : MenagerieCard(NAME, CardType.Action, 4), AfterCardGainedListenerForSelf, UsesHorses, DiscardCardsForBenefitActionCard {

    init {
        addCards = 1
        addActions = 2
        special = "When you gain this, you may discard any number of Treasures, revealed, to gain that many Horses."
    }

    override fun afterCardGained(player: Player) {
        val numTreasures = player.hand.count { it.isTreasure }
        if (numTreasures > 0) {
            player.optionallyDiscardCardsForBenefit(this, numTreasures, "You may discard any number of Treasures to gain that many Horses", null) { it.isTreasure }
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        repeat(discardedCards.size) {
            player.gainHorse()
        }
    }

    companion object {
        const val NAME: String = "Hostelry"
    }
}

