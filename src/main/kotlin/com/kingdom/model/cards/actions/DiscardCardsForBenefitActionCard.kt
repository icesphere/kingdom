package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card

interface DiscardCardsForBenefitActionCard {
    fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?)
    fun onChoseDoNotUse(player: Player)
}