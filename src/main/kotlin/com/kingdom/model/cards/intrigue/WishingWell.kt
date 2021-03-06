package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class WishingWell : IntrigueCard(NAME, CardType.Action, 3), ChooseCardActionCard {

    init {
        addActions = 1
        addCards = 1
        special = "Name a card, then reveal the top card of your deck. If you name it, put it in your hand."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cardsToSelectFrom = player.game.allCardsCopy
        player.chooseCardAction(special, this, cardsToSelectFrom, false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addEventLogWithUsername("named ${card.cardNameWithBackgroundColor}")

        val topCards = player.revealTopCardsOfDeck(1)
        if (topCards.isNotEmpty()) {
            if (topCards.first().name == card.name) {
                player.showInfoMessage("You guessed correctly")
                player.addEventLogWithUsername("correctly guessed the top card of their deck was ${card.cardNameWithBackgroundColor} and added it to their hand")
                player.removeTopCardOfDeck()
                player.addCardToHand(card)
            } else {
                player.showInfoMessage("You guessed wrong. The top card was ${topCards.first().cardNameWithBackgroundColor}.")
            }
        } else {
            player.showInfoMessage("Your deck is empty")
        }
    }

    companion object {
        const val NAME: String = "Wishing Well"
    }
}

