package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player

interface TavernCard {

    fun isTavernCardActionable(player: Player): Boolean

    fun onTavernCardCalled(player: Player)

}