package com.kingdom.model.cards.darkages

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Squire : DarkAgesCard(NAME, CardType.Action, 2), ChoiceActionCard, AfterCardTrashedListenerForSelf {

    init {
        addCoins = 1
        special = "Choose one: +2 Actions; or +2 Buys; or gain a Silver. When you trash this, gain an Attack card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "+2 Actions"), Choice(2, "+2 Buys"), Choice(3, "Gain Silver"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> player.addActions(2)
            2 -> player.addBuys(2)
            3 -> player.gainSupplyCard(Silver(), true)
        }
    }

    override fun afterCardTrashed(player: Player) {
        if (player.game.availableCards.any { it.isAttack }) {
            player.chooseSupplyCardToGain({ c -> c.isAttack }, "Gain an Attack card from the supply")
        }
    }

    companion object {
        const val NAME: String = "Squire"
    }
}

