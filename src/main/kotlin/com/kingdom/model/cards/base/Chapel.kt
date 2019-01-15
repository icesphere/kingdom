package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Chapel: BaseCard(NAME, CardType.Action, 2) {
    init {
        special = "Trash up to 4 cards from your hand."
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHand(4, special)
    }

    companion object {
        const val NAME: String = "Chapel"
    }
}