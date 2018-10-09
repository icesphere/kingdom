package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Masquerade : IntrigueCard(NAME, CardType.Action, 3) {

    init {
        testing = true
        addCards = 2
        special = "Each player with any cards in hand passes one to the next such player to their left, at once. Then you may trash a card from your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.game.players.forEach {
            if (it.hand.isNotEmpty()) {
                it.passCardFromHandToPlayerOnLeft()
            }
        }
        player.waitForOtherPlayersToResolveActions()
        player.trashCardFromHand(true)
    }

    companion object {
        const val NAME: String = "Masquerade"
    }
}

