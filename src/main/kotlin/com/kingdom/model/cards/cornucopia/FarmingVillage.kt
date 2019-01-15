package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class FarmingVillage : CornucopiaCard(NAME, CardType.Action, 4) {

    init {
        addActions = 2
        special = "Reveal cards from the top of your deck until you reveal an Action or Treasure card. Put that card into your hand and discard the rest."
        fontSize = 9
        textSize = 102
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val card = player.revealFromDeckUntilCardFoundAndDiscardOthers { c -> c.isAction || c.isTreasure }

        if (card != null) {
            player.addEventLogWithUsername("added ${card.cardNameWithBackgroundColor} to their hand")
            player.addCardToHand(card)
        }
    }

    companion object {
        const val NAME: String = "Farming Village"
    }
}

