package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class SeaChart : SeasideCard(NAME, CardType.Action, 3) {

    init {
        addCards = 1
        addActions = 1
        special = "Reveal the top card of your deck. If you have a copy of it in play, put it into your hand."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val topCard = player.revealTopCardOfDeck() ?: return
        if (player.inPlayWithDuration.any { it.name == topCard.name }) {
            player.removeCardFromDeck(topCard)
            player.addCardToHand(topCard)
            player.addEventLogWithUsername("put ${topCard.cardNameWithBackgroundColor} into hand with ${cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Sea Chart"
    }
}
