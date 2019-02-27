package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Artificer : AdventuresCard(NAME, CardType.Action, 5), DiscardCardsForBenefitActionCard {

    init {
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "Discard any number of cards. You may gain a card onto your deck costing exactly \$1 per card discarded."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyDiscardCardsForBenefit(this, player.hand.size, special)
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        val cost = discardedCards.size

        if (player.game.availableCards.any { player.getCardCostWithModifiers(it) == cost }) {
            player.chooseSupplyCardToGainWithExactCost(cost, "Gain a free card from the supply to the top of your deck costing $cost", CardLocation.Deck, true)
        } else {
            val message = "There were no available cards costing exactly \$$cost"
            player.showInfoMessage(message)
            player.addInfoLog(message)
        }
    }

    companion object {
        const val NAME: String = "Artificer"
    }
}

