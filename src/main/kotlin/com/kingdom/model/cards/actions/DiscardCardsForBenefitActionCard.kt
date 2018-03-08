package com.kingdom.model.cards.actions

import com.kingdom.model.Player
import com.kingdom.model.cards.Card

interface DiscardCardsForBenefitActionCard {
    fun cardsDiscarded(player: Player, discardedCards: List<Card>)
    fun onChoseDoNotUse(player: Player)
}