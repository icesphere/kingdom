package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Cellar : KingdomCard(NAME, CardType.Action, 2), DiscardCardsForBenefitActionCard {

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        player.drawCards(discardedCards.size)
    }

    override fun onChoseDoNotUse(player: Player, info: Any?) {
        //do nothing
    }

    init {
        addActions = 1
        special = "Discard any number of cards, then draw that many."
        fontSize = 13
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyDiscardCardsForBenefit(this, player.hand.size, special)
    }

    companion object {
        const val NAME: String = "Cellar"
    }
}

