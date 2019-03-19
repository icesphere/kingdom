package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class MountainVillage : RenaissanceCard(NAME, CardType.Action, 4) {

    init {
        addActions = 2
        special = "Look through your discard pile and put a card from it into your hand; if you can’t, +1 Card."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.cardsInDiscard.isEmpty()) {
            player.drawCard()
        } else {
            player.addCardFromDiscardToHand()
            player.addEventLogWithUsername("put a card from their discard pile into their hand")
        }
    }

    companion object {
        const val NAME: String = "Mountain Village"
    }
}