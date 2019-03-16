package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Ducat : RenaissanceCard(NAME, CardType.Treasure, 2), AfterCardGainedListenerForSelf, ChoiceActionCard {

    init {
        addCoffers = 1
        addBuys = 1
        special = "When you gain this, you may trash a Copper from your hand."
    }

    override fun afterCardGained(player: Player) {
        if (player.hand.any { it.isCopper }) {
            player.yesNoChoice(this, "Trash a Copper from your hand?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardFromHand(player.hand.first { it.isCopper })
        }
    }

    companion object {
        const val NAME: String = "Ducat"
    }
}