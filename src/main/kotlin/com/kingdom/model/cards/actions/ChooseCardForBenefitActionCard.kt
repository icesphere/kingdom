package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface ChooseCardForBenefitActionCard {

    fun onCardChosen(player: Player, card: Card)
}