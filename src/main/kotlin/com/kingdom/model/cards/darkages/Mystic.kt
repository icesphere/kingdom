package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Mystic : DarkAgesCard(NAME, CardType.Action, 5), ChooseCardActionCard {

    init {
        addActions = 1
        addCoins = 2
        special = "Name a card, then reveal the top card of your deck. If you named it, put it into your hand."
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
        const val NAME: String = "Mystic"
    }
}

