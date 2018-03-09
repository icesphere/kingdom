package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card

interface DiscardCardsForBenefitActionCard {
    fun cardsDiscarded(player: OldPlayer, discardedCards: List<Card>)
    fun onChoseDoNotUse(player: OldPlayer)
}