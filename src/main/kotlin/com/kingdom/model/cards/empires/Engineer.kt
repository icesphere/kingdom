package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Engineer : EmpiresCard(NAME, CardType.Action, 0, 4), ChoiceActionCard {

    init {
        special = "Gain a card costing up to \$4. You may trash this. If you do, gain a card costing up to \$4."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainWithMaxCost(4)
        player.yesNoChoice(this, "Trash ${this.cardNameWithBackgroundColor} to gain a card costing up to \$4?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardInPlay(this)
            player.chooseSupplyCardToGainWithMaxCost(4)
        }
    }

    companion object {
        const val NAME: String = "Engineer"
    }
}

