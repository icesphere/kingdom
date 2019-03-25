package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Scholar : RenaissanceCard(NAME, CardType.Action, 5) {

    init {
        special = "Discard your hand. +7 Cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardHand()
        player.drawCards(7)
    }

    companion object {
        const val NAME: String = "Scholar"
    }
}