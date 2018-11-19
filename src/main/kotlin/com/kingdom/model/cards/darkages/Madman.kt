package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Madman : DarkAgesCard(NAME, CardType.Action, 0) {

    init {
        addActions = 2
        special = "Return this to the Madman pile. If you do, +1 Card per card in your hand. (This is not in the Supply.)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.removeCardInPlay(this)
        player.game.returnCardToSupply(this)
        player.drawCards(player.hand.size)
    }

    companion object {
        const val NAME: String = "Madman"
    }
}

