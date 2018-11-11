package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class PoorHouse : DarkAgesCard(NAME, CardType.Action, 1) {

    init {
        addCoins = 4
        special = "Reveal your hand. -\$1 per Treasure card in your hand. (You can’t go below \$0.)"
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.revealHand()
        var numCoinsToSubtract = player.hand.count { it.isTreasure }

        if (numCoinsToSubtract > 4) {
            numCoinsToSubtract = 4
        }

        player.addCoins(numCoinsToSubtract * -1)
    }

    companion object {
        const val NAME: String = "Poor House"
    }
}

