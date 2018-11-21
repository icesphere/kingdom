package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class BanditCamp : DarkAgesCard(NAME, CardType.Action, 5) {

    init {
        testing = true
        addCards = 1
        addActions = 2
        special = "Gain a Spoils from the Spoils pile."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSpoils()
    }

    companion object {
        const val NAME: String = "Bandit Camp"
    }
}

