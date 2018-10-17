package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Masquerade : IntrigueCard(NAME, CardType.Action, 3) {

    init {
        addCards = 2
        special = "Each player with any cards in hand passes one to the next such player to their left, at once. Then you may trash a card from your hand."
        textSize = 102
        fontSize = 11
    }

    override val isTrashingCard: Boolean = true

    override fun cardPlayedSpecialAction(player: Player) {
        player.game.players.forEach {
            if (it.hand.isNotEmpty()) {
                //todo this shouldn't pass the card until all cards have been passed
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

