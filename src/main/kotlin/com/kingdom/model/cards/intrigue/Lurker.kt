package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.kingdom.KingdomCard
import com.kingdom.model.players.Player

class Lurker : IntrigueCard(NAME, CardType.Action, 2) {
    init {
        disabled = true
        addActions = 1
        special = "Choose one: Trash an Action card from the Supply; or gain an Action card from the trash."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Lurker"
    }
}

