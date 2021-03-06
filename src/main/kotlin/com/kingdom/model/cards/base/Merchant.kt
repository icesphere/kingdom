package com.kingdom.model.cards.base

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.CardPlayedListener
import com.kingdom.model.players.Player

class Merchant : BaseCard(NAME, CardType.Action, 3), CardPlayedListener {

    var firstSilverPlayed: Boolean = false

    init {
        addCards = 1
        addActions = 1
        isAddCoinsCard = true
        special = "The first time you play a Silver this turn, +\$1."
    }

    override fun onCardPlayed(card: Card, player: Player) {
        if (card.isSilver && !firstSilverPlayed) {
            firstSilverPlayed = true
            player.addCoins(1)
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        firstSilverPlayed = false
    }

    companion object {
        const val NAME: String = "Merchant"
    }
}

