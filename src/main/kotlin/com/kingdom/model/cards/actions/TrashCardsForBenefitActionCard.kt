package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card

interface TrashCardsForBenefitActionCard {
    fun cardsScrapped(player: Player, scrappedCards: List<Card>)
    fun isCardApplicable(card: Card): Boolean
}