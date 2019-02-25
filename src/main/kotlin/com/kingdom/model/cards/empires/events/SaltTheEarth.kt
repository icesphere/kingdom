package com.kingdom.model.cards.empires.events

import com.kingdom.model.players.Player

class SaltTheEarth : EmpiresEvent(NAME, 4) {

    init {
        addVictoryCoins = 1
        special = "Trash a Victory card from the Supply."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardFromSupply(false, { c -> c.isVictory })
    }

    companion object {
        const val NAME: String = "Salt the Earth"
    }
}