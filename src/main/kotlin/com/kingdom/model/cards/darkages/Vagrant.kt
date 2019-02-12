package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Vagrant : DarkAgesCard(NAME, CardType.Action, 2) {

    init {
        addCards = 1
        addActions = 1
        special = "Reveal the top card of your deck. If it’s a Curse, Ruins, Shelter, or Victory card, put it into your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val card = player.revealTopCardOfDeck()
        if (card != null) {
            player.showInfoMessage("Revealed ${card.cardNameWithBackgroundColor}")
            if (card.isCurse || card.isRuins || card.isShelter || card.isVictory) {
                player.addCardToHand(player.removeTopCardOfDeck()!!, true)
            }
        } else {
            player.showInfoMessage("Deck was empty")
        }
    }

    companion object {
        const val NAME: String = "Vagrant"
    }
}

