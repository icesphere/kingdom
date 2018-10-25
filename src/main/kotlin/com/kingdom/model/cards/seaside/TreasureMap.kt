package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class TreasureMap : SeasideCard(NAME, CardType.Action, 4) {

    init {
        special = "Trash this and a Treasure Map from your hand. If you trashed two Treasure Maps, gain 4 Golds onto your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardInPlay(this)

        if (player.hand.any { it.name == TreasureMap.NAME }) {
            player.trashCardFromHand(player.hand.first { it.name == TreasureMap.NAME })

            repeat(4) {
                player.acquireFreeCardFromSupply(Gold(), false, CardLocation.Deck)
            }
        }
    }

    companion object {
        const val NAME: String = "Treasure Map"
    }
}

