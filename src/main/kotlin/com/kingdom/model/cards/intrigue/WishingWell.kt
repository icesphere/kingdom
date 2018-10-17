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
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cardsToSelectFrom = player.game.kingdomCards + player.game.supplyCards
        player.chooseCardAction(special, this, cardsToSelectFrom, false)
    }

    override fun onCardChosen(player: Player, card: Card) {
        val topCards = player.revealTopCardsOfDeck(1)
        if (topCards.isNotEmpty()) {
            if (topCards.first().name == card.name) {
                player.addUsernameGameLog("correctly guessed the top card of their deck was ${card.cardNameWithBackgroundColor} and added it to their hand")
                player.removeTopCardOfDeck()
                player.addCardToHand(card)
            }
        }
    }

    companion object {
        const val NAME: String = "Wishing Well"
    }
}

