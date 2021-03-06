package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Smugglers : SeasideCard(NAME, CardType.Action, 3), ChooseCardActionCard {

    init {
        special = "Gain a copy of a card costing up to \$6 that the player to your right gained on their last turn."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val previousPlayerCardsGainsLastTurn = player.game.previousPlayer?.lastTurnSummary?.cardsGained

        if (previousPlayerCardsGainsLastTurn?.isNotEmpty() == true) {
            val cards = previousPlayerCardsGainsLastTurn.filter { player.game.isCardAvailableInSupply(it) && player.getCardCostWithModifiers(it) <= 6 }
            if (cards.isNotEmpty()) {
                player.chooseCardAction(special, this, cards, false)
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addEventLogWithUsername("used ${this.cardNameWithBackgroundColor} to gain a copy of ${card.cardNameWithBackgroundColor}")
        player.gainSupplyCard(card)
    }

    companion object {
        const val NAME: String = "Smugglers"
    }
}

