package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Menagerie : CornucopiaCard(NAME, CardType.Action, 3) {

    init {
        addActions = 1
        special = "Reveal your hand. If the revealed cards all have different names, +3 Cards. Otherwise, +1 Card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.revealHand()
            val groupedHand = player.hand.groupBy { it.name }
            if (groupedHand.size == player.hand.size) {
                player.drawCards(3)
            } else {
                player.drawCard()
            }
        }
    }

    companion object {
        const val NAME: String = "Menagerie"
    }
}

