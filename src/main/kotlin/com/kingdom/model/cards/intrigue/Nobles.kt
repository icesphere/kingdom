package com.kingdom.model.cards.intrigue

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Nobles : IntrigueCard(NAME, CardType.ActionVictory, 6), ChoiceActionCard {

    init {
        testing = true
        victoryPoints = 2
        special = "Choose one: +3 Cards; or +2 Actions."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "+3 Cards"), Choice(2, "+2 Actions"))
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            player.drawCards(3)
        } else {
            player.addActions(2)
        }
    }

    companion object {
        const val NAME: String = "Nobles"
    }
}

