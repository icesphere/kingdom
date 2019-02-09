package com.kingdom.model.cards.empires.castles

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.empires.EmpiresCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.supply.Duchy
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class SprawlingCastle : EmpiresCard(NAME, CardType.VictoryCastle, 8), AfterCardGainedListenerForSelf, ChoiceActionCard {

    init {
        victoryPoints = 4
        special = "When you gain this, gain a Duchy or 3 Estates."
        fontSize = 8
    }

    override fun afterCardGained(player: Player) {
        player.makeChoice(this, Choice(1, "Gain Duchy"), Choice(2, "Gain 3 Estates"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.gainSupplyCard(Duchy(), true)
        } else {
            player.gainSupplyCard(Estate(), true)
            player.gainSupplyCard(Estate(), true)
            player.gainSupplyCard(Estate(), true)
        }
    }

    override val pileName: String
        get() = Castles.NAME

    companion object {
        const val NAME: String = "Sprawling Castle"
    }
}

