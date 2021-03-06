package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Patrol : IntrigueCard(NAME, CardType.Action, 5) {

    init {
        addCards = 3
        special = "Reveal the top 4 cards of your deck. Put the Victory cards and Curses into your hand. Put the rest back in any order."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.revealTopCardsOfDeck(4)
        player.showInfoMessage("Revealed ${cards.groupedString}")

        val cardsToPutBack = mutableListOf<Card>()

        cards.forEach {
            player.removeCardFromDeck(it)
            if (it.isVictory || it.isCurse) {
                player.addCardToHand(it)
            } else {
                cardsToPutBack.add(it)
            }
        }

        if (cardsToPutBack.isNotEmpty()) {
            if (cardsToPutBack.size == 1) {
                player.addCardToTopOfDeck(cardsToPutBack[0], false)
            } else {
                player.putCardsOnTopOfDeckInAnyOrder(cardsToPutBack)
            }
        }
    }

    companion object {
        const val NAME: String = "Patrol"
    }
}

