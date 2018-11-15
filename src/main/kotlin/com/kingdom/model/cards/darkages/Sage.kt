package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Sage : DarkAgesCard(NAME, CardType.Action, 3) {

    init {
        addActions = 1
        special = "Reveal cards from your deck until you reveal one costing \$3 or more. Put that card into your hand and discard the rest."
        textSize = 97
    }

    override fun cardPlayedSpecialAction(player: Player) {

        val card = player.revealFromDeckUntilCardFoundAndDiscardOthers { c -> player.getCardCostWithModifiers(c) >= 3 }

        if (card != null) {
            player.addCardToHand(card, true)
        } else {
            val message = "No card costing \$3 or more found"
            player.addUsernameGameLog(message)
            player.showInfoMessage(message)
        }
    }

    companion object {
        const val NAME: String = "Sage"
    }
}

