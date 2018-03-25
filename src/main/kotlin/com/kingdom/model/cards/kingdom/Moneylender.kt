package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.players.Player

class Moneylender : KingdomCard(NAME, CardType.Action, 4), ChoiceActionCard {
    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            val copper = player.hand.first { it is Copper }
            player.trashCardFromHand(copper)
            player.coins += 3
        }
    }

    init {
        special = "You may trash a Copper from your hand. If you do, +\$3."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it is Copper }) {
            player.yesNoChoice(this, special)
        }
    }

    companion object {
        const val NAME: String = "Moneylender"
    }
}

