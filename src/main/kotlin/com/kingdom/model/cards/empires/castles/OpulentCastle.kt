package com.kingdom.model.cards.empires.castles

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.cards.empires.EmpiresCard
import com.kingdom.model.players.Player

class OpulentCastle : EmpiresCard(NAME, CardType.ActionVictoryCastle, 7), DiscardCardsForBenefitActionCard {

    init {
        victoryPoints = 3
        isAddCoinsCard = true
        special = "Discard any number of Victory cards. +\$2 per card discarded."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyDiscardCardsForBenefit(this, player.hand.size, special)
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        player.addCoins(discardedCards.size * 2)
    }

    override val pileName: String
        get() = Castles.NAME

    companion object {
        const val NAME: String = "Opulent Castle"
    }
}

