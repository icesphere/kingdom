package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class TradeRoute : ProsperityCard(NAME, CardType.Action, 3) {

    init {
        testing = true
        addBuys = 1
        special = "Trash a card from your hand. +\$1 per Coin token on the Trade Route mat. Setup: Add a Coin token to each Victory Supply pile. When a card is gained from that pile, move the token to the Trade Route mat."
        isTrashingCard = true
        fontSize = 10
        textSize = 80
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Trade Route"
    }
}

