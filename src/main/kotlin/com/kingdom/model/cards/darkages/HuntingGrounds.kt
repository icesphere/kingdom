package com.kingdom.model.cards.darkages

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.cards.supply.Duchy
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.players.Player

class HuntingGrounds : DarkAgesCard(NAME, CardType.Action, 6), AfterCardTrashedListenerForSelf, ChoiceActionCard {

    init {
        testing = true
        addCards = 4
        special = "When you trash this, gain a Duchy or 3 Estates."
        fontSize = 9
    }

    override fun afterCardTrashed(player: Player) {
        player.makeChoice(this, Choice(1, "Gain Duchy"), Choice(2, "Gain 3 Estates"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.gainSupplyCard(Duchy(), true)
        } else {
            repeat(3) {
                player.gainSupplyCard(Estate(), true)
            }
        }
    }

    companion object {
        const val NAME: String = "Hunting Grounds"
    }
}

