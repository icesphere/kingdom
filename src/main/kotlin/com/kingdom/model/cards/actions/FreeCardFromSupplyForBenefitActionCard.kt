package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface FreeCardFromSupplyForBenefitActionCard {

    fun onCardAcquired(player: Player, card: Card)
}