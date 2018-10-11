package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.CardPlayedListener
import com.kingdom.model.players.Player

class Merchant : KingdomCard(NAME, CardType.Action, 3), CardPlayedListener {

    var firstSilverPlayed: Boolean = false

    init {
        testing = true
        addCards = 1
        addActions = 1
        special = "The first time you play a Silver this turn, +\$1."
        isPlayTreasureCardsRequired = true
    }

    override fun onCardPlayed(card: Card, player: Player) {
        if (card.isSilver && !firstSilverPlayed) {
            firstSilverPlayed = true
            player.addCoins(1)
        }
    }

    companion object {
        const val NAME: String = "Merchant"
    }
}

