package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class BustlingVillage : EmpiresCard(NAME, CardType.Action, 5) {

    init {
        addCards = 1
        addActions = 3
        special = "Look through your discard pile. You may reveal a Settlers from it and put it into your hand. (Bustling Village is the bottom half of the Settlers pile.)"
        textSize = 80
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    override val pileName: String
        get() = Settlers.NAME

    companion object {
        const val NAME: String = "Bustling Village"
    }
}

