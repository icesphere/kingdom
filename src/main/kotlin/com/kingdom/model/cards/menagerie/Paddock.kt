package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player
import com.kingdom.util.plural

class Paddock : MenagerieCard(NAME, CardType.Action, 5), UsesHorses {

    init {
        addCoins = 2
        special = "Gain 2 Horses. +1 Action per empty Supply pile."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainHorse()
        player.gainHorse()
        val emptyPiles = player.game.numEmptyPiles
        if (emptyPiles > 0) {
            player.addActions(emptyPiles)
            player.addEventLogWithUsername("gained +${"Action".plural(emptyPiles)} from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Paddock"
    }
}

