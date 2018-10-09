package com.kingdom.model.cards.intrigue

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.kingdom.KingdomCard
import com.kingdom.model.players.Player

class Pawn : IntrigueCard(NAME, CardType.Action, 2), ChoiceActionCard {

    init {
        testing = true
        special = "Choose two: +1 Card; +1 Action; +1 Buy; +\$1. The choices must be different."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this,
                Choice(1, "+1 Card, +1 Action"),
                Choice(2, "+1 Card, +1 Buy"),
                Choice(3, "+1 Card, +\$1"),
                Choice(4, "+1 Action, +1 Buy"),
                Choice(5, "+1 Action, +\$1"),
                Choice(6, "+1 Buy, +\$1")
        )
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        when (choice) {
            1 -> {
                player.drawCard()
                player.addActions(1)
            }
            2 -> {
                player.drawCard()
                player.addBuys(1)
            }
            3 -> {
                player.drawCard()
                player.addCoins(1)
            }
            4 -> {
                player.addActions(1)
                player.addBuys(1)
            }
            5 -> {
                player.addActions(1)
                player.addCoins(1)
            }
            6 -> {
                player.addBuys(1)
                player.addCoins(1)
            }
        }
    }

    companion object {
        const val NAME: String = "Pawn"
    }
}

