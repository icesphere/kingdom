package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class ActingTroupe : RenaissanceCard(NAME, CardType.Action, 3) {

    init {
        addVillagers = 4
        special = "Trash this."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardInPlay(this, true)
    }

    companion object {
        const val NAME: String = "Acting Troupe"
    }
}