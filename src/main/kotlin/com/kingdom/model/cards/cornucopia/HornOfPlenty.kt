package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.players.Player

class HornOfPlenty : CornucopiaCard(NAME, CardType.Treasure, 5), FreeCardFromSupplyForBenefitActionCard {

    init {
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        special = "When you play this, gain a card costing up to \$1 per differently named card you have in play (counting this). If it’s a Victory card, trash this."
        fontSize = 9
        textSize = 79
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val numDifferentCards = player.inPlay.groupBy { it.name }.size

        player.acquireFreeCardForBenefit(numDifferentCards, "Gain a card costing up to \$$numDifferentCards. If it’s a Victory card, ${this.cardNameWithBackgroundColor} will be trashed.", this)

        val cards = player.removeTopCardsOfDeck(4, revealCards = true)

        player.addCardsToDiscard(cards)

        val groupedCards = cards.groupBy { it.name }

        player.addCoins(groupedCards.size)
    }

    override fun onCardAcquired(player: Player, card: Card) {
        if (card.isVictory) {
            player.trashCardInPlay(this)
        }
    }

    companion object {
        const val NAME: String = "Horn of Plenty"
    }
}

