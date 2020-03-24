package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class HuntingLodge : MenagerieCard(NAME, CardType.Action, 5), ChoiceActionCard {

    init {
        addCards = 1
        addActions = 2
        special = "You may discard your hand for +5 Cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.yesNoChoice(this, "Discard your hand for +5 cards?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardHand()
            player.drawCards(5)
        }
    }

    companion object {
        const val NAME: String = "Hunting Lodge"
    }
}

