package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Harvest : CornucopiaCard(NAME, CardType.Action, 5) {

    init {
        isAddCoinsCard = true
        special = "Reveal the top 4 cards of your deck, then discard them. +\$1 per differently named card revealed."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(4, revealCards = true)

        player.addCardsToDiscard(cards)

        val groupedCards = cards.groupBy { it.name }

        player.addCoins(groupedCards.size)
    }

    companion object {
        const val NAME: String = "Harvest"
    }
}

