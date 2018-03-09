package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card

interface TrashCardsForBenefitActionCard {
    fun cardsScrapped(player: OldPlayer, scrappedCards: List<Card>)
    fun isCardApplicable(card: Card): Boolean
}