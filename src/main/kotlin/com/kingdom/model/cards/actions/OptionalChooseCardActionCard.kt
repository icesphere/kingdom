package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player

interface OptionalChooseCardActionCard : ChooseCardActionCard {

    fun onCardNotChosen(player: Player, info: Any?)
}