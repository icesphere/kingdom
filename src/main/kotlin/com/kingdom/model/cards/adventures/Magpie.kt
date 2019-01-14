package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Magpie : AdventuresCard(NAME, CardType.Action, 4) {

    init {
        addCards = 1
        addActions = 1
        special = "Reveal the top card of your deck. If it’s a Treasure, put it into your hand. If it’s an Action or Victory card, gain a Magpie."
        textSize = 84
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val card = player.revealTopCardOfDeck()
        if (card != null) {
            player.showInfoMessage("Revealed card was ${card.cardNameWithBackgroundColor}")
            if (card.isTreasure) {
                player.removeTopCardOfDeck()
                player.addCardToHand(card, true)
            } else if (card.isAction || card.isVictory) {
                player.gainSupplyCard(Magpie(), true)
            }
        }
    }

    companion object {
        const val NAME: String = "Magpie"
    }
}

