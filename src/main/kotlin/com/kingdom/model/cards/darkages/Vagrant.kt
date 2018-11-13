package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Vagrant : DarkAgesCard(NAME, CardType.Action, 2) {

    init {
        testing = true
        addCards = 1
        addActions = 1
        special = "Reveal the top card of your deck. If it’s a Curse, Ruins, Shelter, or Victory card, put it into your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.revealTopCardsOfDeck(1)
        if (cards.isNotEmpty()) {
            val card = cards.first()
            if (card.isCurse || card.isRuins || card.isShelter || card.isVictory) {
                player.addCardToHand(player.removeTopCardOfDeck()!!, true)
            }
        }
    }

    companion object {
        const val NAME: String = "Vagrant"
    }
}

