package com.kingdom.model.cards.empires

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.players.Player

class WildHunt : EmpiresCard(NAME, CardType.ActionGathering, 5), ChoiceActionCard {

    init {
        special = "Choose one: +3 Cards and add 1 VP to the Wild Hunt Supply pile; or gain an Estate, and if you do, take the VP from the pile."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, special, Choice(1, "+3 Cards, add VP to pile"), Choice(2, "Gain Estate, take VP from pile"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.drawCards(3)
            player.game.addVictoryPointToSupplyPile(pileName)
            player.addEventLogWithUsername("gained +3 Cards and added 1 VP to $cardNameWithBackgroundColor Supply pile")
        } else {
            player.gainSupplyCard(Estate(), true)
            player.takeVictoryPointsFromSupplyPile(this)
        }
    }

    companion object {
        const val NAME: String = "Wild Hunt"
    }
}

