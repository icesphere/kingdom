package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class HuntingParty : CornucopiaCard(NAME, CardType.Action, 5) {

    init {
        addCards = 1
        addActions = 1
        special = "Reveal your hand. Reveal cards from your deck until you reveal one that isn’t a copy of one in your hand. Put it into your hand and discard the rest."
        fontSize = 10
        textSize = 71
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.revealHand()

        val handCardNames = player.hand.distinctBy { it.name }.map { it.name }

        val card = player.revealFromDeckUntilCardFoundAndDiscardOthers { c -> !handCardNames.contains(c.name) }

        if (card != null) {
            player.addCardToHand(card, true)
        } else {
            player.addEventLogWithUsername("Deck did not contain any cards not already in ${player.username}'s hand")
            player.showInfoMessage("Deck did not contain any cards not already in your hand")
        }
    }

    companion object {
        const val NAME: String = "Hunting Party"
    }
}

