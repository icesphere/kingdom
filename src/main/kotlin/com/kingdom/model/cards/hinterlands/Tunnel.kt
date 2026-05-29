package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardDiscardedListenerForSelf
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Tunnel : HinterlandsCard(NAME, CardType.VictoryReaction, 3), AfterCardDiscardedListenerForSelf, ChoiceActionCard {

    init {
        victoryPoints = 2
        special = "When you discard this other than during Clean-up, you may reveal it to gain a Gold."
        fontSize = 11
    }

    override fun afterCardDiscarded(player: Player) {
        player.yesNoChoice(this, "Reveal ${cardNameWithBackgroundColor} to gain a ${Gold().cardNameWithBackgroundColor}?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addEventLogWithUsername("revealed ${cardNameWithBackgroundColor}")
            player.gainSupplyCard(Gold(), true)
        }
    }

    companion object {
        const val NAME: String = "Tunnel"
    }
}
