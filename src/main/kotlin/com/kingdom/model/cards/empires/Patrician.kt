package com.kingdom.model.cards.empires

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.MultiTypePile
import com.kingdom.model.players.Player

class Patrician : EmpiresCard(NAME, CardType.Action, 2), MultiTypePile {

    init {
        addCards = 1
        addActions = 1
        special = "Reveal the top card of your deck. If it costs \$5 or more, put it into your hand. (Patrician is the top half of the Emporium pile.)"
    }

    override val otherCardsInPile: List<Card>
        get() = listOf(Emporium())

    override fun createMultiTypePile(game: Game): List<Card> {
        return listOf(
                Patrician(),
                Patrician(),
                Patrician(),
                Patrician(),
                Patrician(),
                Emporium(),
                Emporium(),
                Emporium(),
                Emporium(),
                Emporium()
        )
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val card = player.revealTopCardOfDeck()
        if (card != null) {
            player.showInfoMessage("Revealed ${card.cardNameWithBackgroundColor}")
            if (player.getCardCostWithModifiers(card) >= 5) {
                player.addCardToHand(player.removeTopCardOfDeck()!!, true)
            }
        } else {
            player.showInfoMessage("Deck was empty")
        }
    }

    companion object {
        const val NAME: String = "Patrician"
    }
}

