package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Smugglers : SeasideCard(NAME, CardType.Action, 3), ChooseCardActionCard {

    init {
        testing = true
        special = "Gain a copy of a card costing up to \$6 that the player to your right gained on their last turn."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val previousPlayerCardsGainsLastTurn = player.game.previousPlayer?.lastTurnSummary?.cardsAcquired

        if (previousPlayerCardsGainsLastTurn?.isNotEmpty() == true) {
            val cards = previousPlayerCardsGainsLastTurn.filter { player.game.isCardAvailableInSupply(it) && player.getCardCostWithModifiers(it) <= 6 }
            if (cards.isNotEmpty()) {
                player.chooseCardAction(special, this, cards, false)
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card) {
        player.addUsernameGameLog("used ${this.cardNameWithBackgroundColor} to gain a copy of ${card.cardNameWithBackgroundColor}")
        player.acquireFreeCardFromSupply(card)
    }

    companion object {
        const val NAME: String = "Smugglers"
    }
}

