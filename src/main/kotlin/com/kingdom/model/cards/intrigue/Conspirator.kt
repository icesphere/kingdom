package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Conspirator : IntrigueCard(NAME, CardType.Action, 4) {

    init {
        addCoins = 2
        special = "If you’ve played 3 or more Actions this turn (counting this), +1 Card and +1 Action."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.numActionsPlayed >= 3) {
            player.drawCard()
            player.addActions(1)
        }
    }

    companion object {
        const val NAME: String = "Conspirator"
    }
}

