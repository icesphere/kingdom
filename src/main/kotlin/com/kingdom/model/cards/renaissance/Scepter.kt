package com.kingdom.model.cards.renaissance

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Scepter : RenaissanceCard(NAME, CardType.Treasure, 5), ChoiceActionCard {

    init {
        disabled = true
        special = "When you play this, chose one: +\$2; or replay an Action card you played this turn that’s still in play."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "+\$2"), Choice(2, "Replay Action"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addCoins(2)
        } else {
            //todo
        }
    }

    companion object {
        const val NAME: String = "Scepter"
    }
}