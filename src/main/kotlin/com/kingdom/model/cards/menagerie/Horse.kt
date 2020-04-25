package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Horse : MenagerieCard(NAME, CardType.Action, 3) {

    init {
        addCards = 2
        addActions = 1
        special = "Return this to its pile. (This is not in the Supply)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.removeCardInPlay(this, CardLocation.Supply)
        player.game.returnCardToSupply(this)
    }

    companion object {
        const val NAME: String = "Horse"
    }
}

