package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Chapel: KingdomCard(NAME, CardType.Action, 2) {
    init {
        special = "Trash up to 4 cards from your hand."
    }

    override val isTrashingCard: Boolean = true

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHand(4, special)
    }

    companion object {
        const val NAME: String = "Chapel"
    }
}