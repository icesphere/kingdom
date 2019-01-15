package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class MiningVillage : IntrigueCard(NAME, CardType.Action, 4), ChoiceActionCard {

    init {
        addCards = 1
        addActions = 2
        special = "You may trash this for +\$2."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.yesNoChoice(this, "Trash $cardNameWithBackgroundColor for +\$2?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addEventLogWithUsername("trashed $cardNameWithBackgroundColor for +\$2")
            player.trashCardInPlay(this)
            player.addCoins(2)
        }
    }

    companion object {
        const val NAME: String = "Mining Village"
    }
}

