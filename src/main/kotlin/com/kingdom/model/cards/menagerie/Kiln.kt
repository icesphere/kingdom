package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.BeforeCardPlayedListenerForCardsInPlay
import com.kingdom.model.players.Player

class Kiln : MenagerieCard(NAME, CardType.Action, 5), BeforeCardPlayedListenerForCardsInPlay, ChoiceActionCard {

    private var used: Boolean = false

    init {
        addCoins = 2
        special = "The next time you play a card this turn, you may first gain a copy of it."
    }

    override fun onBeforeCardPlayed(card: Card, player: Player) {
        if (!used && this != card) {
            used = true
            if (player.game.isCardAvailableInSupply(card)) {
                player.yesNoChoice(this, "Gain a copy of ${card.cardNameWithBackgroundColor}?", card)
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val card = info as Card
            player.addEventLogWithUsername("used ${this.cardNameWithBackgroundColor} to gain a copy of ${card.cardNameWithBackgroundColor}")
            player.gainSupplyCard(card)
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        used = false
    }

    companion object {
        const val NAME: String = "Kiln"
    }
}

