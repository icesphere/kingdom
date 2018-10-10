package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.BeforeOpponentCardPlayedListener
import com.kingdom.model.players.Player

class Diplomat : IntrigueCard(NAME, CardType.ActionReaction, 4), BeforeOpponentCardPlayedListener, ChoiceActionCard {

    init {
        testing = true
        addCards = 2
        special = "If you have 5 or fewer cards in hand (after drawing), +2 Actions. When another player plays an Attack card, you may reveal this from a hand of 5 or more cards, to draw 2 cards then discard 3."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.size <= 5) {
            player.addActions(2)
        }
    }

    override fun onBeforeOpponentCardPlayed(card: Card, player: Player, opponent: Player) {
        if (player.hand.size >= 5) {
            if (card.isAttack && this.location == CardLocation.Hand) {
                player.yesNoChoice(this, "Reveal $cardNameWithBackgroundColor to draw 2 cards then discard 3?")
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            player.drawCards(2)
            player.discardCardsFromHand(3, false)
        }
    }

    companion object {
        const val NAME: String = "Diplomat"
    }
}

