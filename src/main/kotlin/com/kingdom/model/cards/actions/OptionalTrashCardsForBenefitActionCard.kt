package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card

interface OptionalTrashCardsForBenefitActionCard : TrashCardsForBenefitActionCard {

    fun onCardsNotTrashed(player: Player)

}