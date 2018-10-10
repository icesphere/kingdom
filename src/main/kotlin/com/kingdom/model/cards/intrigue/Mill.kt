package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Mill : IntrigueCard(NAME, CardType.ActionVictory, 4), ChoiceActionCard {

    init {
        testing = true
        addCards = 1
        addActions = 1
        victoryPoints = 1
        special = "You may discard 2 cards, for +\$2."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.size >= 2) {
            player.yesNoChoice(this, "Discard 2 cards for +\$2?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            player.discardCardsFromHand(2, false)
            player.addCoins(2)
        }
    }

    companion object {
        const val NAME: String = "Mill"
    }
}

