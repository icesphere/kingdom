package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class JunkDealer : DarkAgesCard(NAME, CardType.Action, 5) {

    init {
        testing = true
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "Trash a card from your hand."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardFromHand(false)
    }

    companion object {
        const val NAME: String = "Junk Dealer"
    }
}

