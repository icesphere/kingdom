package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class WanderingMinstrel : DarkAgesCard(NAME, CardType.Action, 4) {

    init {
        addCards = 1
        addActions = 2
        special = "Reveal the top 3 cards of your deck. Put the Actions back on top in any order and discard the rest."
        fontSize = 9
        nameLines = 2
        textSize = 60
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(3, true)
        player.showInfoMessage("Revealed ${cards.groupedString}")
        val actionCards = cards.filter { it.isAction }
        val nonActionCards = cards - actionCards
        player.addCardsToDiscard(nonActionCards)
        if (actionCards.isNotEmpty()) {
            player.putCardsOnTopOfDeckInAnyOrder(actionCards)
        }
    }

    companion object {
        const val NAME: String = "Wandering Minstrel"
    }
}

