package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Storeroom : DarkAgesCard(NAME, CardType.Action, 3), DiscardCardsForBenefitActionCard {

    init {
        testing = true
        addBuys = 1
        special = "Discard any number of cards, then draw that many. Then discard any number of cards for +\$1 each."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyDiscardCardsForBenefit(this, player.hand.size, "Discard any number of cards, then draw that many", true)
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        val discardToGainCards = info as Boolean

        if (discardToGainCards) {
            if (discardedCards.isNotEmpty()) {
                player.drawCards(discardedCards.size)
            }
            player.optionallyDiscardCardsForBenefit(this, player.hand.size, "Discard any number of cards for +\$1 each", false)
        } else {
            if (discardedCards.isNotEmpty()) {
                player.addCoins(discardedCards.size)
            }
        }
    }

    override fun onChoseDoNotUse(player: Player) {
        //do nothing
    }

    companion object {
        const val NAME: String = "Storeroom"
    }
}

