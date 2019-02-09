package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Vault : ProsperityCard(NAME, CardType.Action, 5), DiscardCardsForBenefitActionCard, ChoiceActionCard {

    init {
        addCards = 2
        special = "Discard any number of cards for +\$1 each. Each other player may discard 2 cards, to draw a card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyDiscardCardsForBenefit(this, player.hand.size, "Discard any number of cards for +\$1 each")

        for (opponent in player.opponentsInOrder) {
            if (opponent.hand.size >= 2) {
                opponent.yesNoChoice(this, "Discard 2 cards to draw a card?")
            }
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        if (player.isYourTurn) {
            if (discardedCards.isNotEmpty()) {
                player.addEventLogWithUsername("Gained +\$${discardedCards.size} from ${this.cardNameWithBackgroundColor}")
                player.addCoins(discardedCards.size)
            }
        } else {
            player.drawCard()
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardCardsForBenefit(this, 2, "")
        }
    }

    companion object {
        const val NAME: String = "Vault"
    }
}

