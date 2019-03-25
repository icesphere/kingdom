package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Seer : RenaissanceCard(NAME, CardType.Action, 5) {

    init {
        addCards = 1
        addActions = 1
        special = "Reveal the top 3 cards of your deck. Put the ones costing from \$2 to \$4 into your hand. Put the rest back in any order."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(3, true)

        val twoToFour = cards.filter { player.getCardCostWithModifiers(it) in 2..4 }
        if (twoToFour.isNotEmpty()) {
            player.addCardsToHand(twoToFour, true)
            player.showInfoMessage("${twoToFour.groupedString} added to your hand")
        }

        val others = cards - twoToFour
        if (others.size == 1) {
            player.addCardToTopOfDeck(others.first(), false)
        } else if (others.size > 1) {
            player.putCardsOnTopOfDeckInAnyOrder(others)
        }
    }

    companion object {
        const val NAME: String = "Seer"
    }
}