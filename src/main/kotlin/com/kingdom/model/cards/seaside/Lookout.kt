package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Lookout : SeasideCard(NAME, CardType.Action, 3), ChooseCardActionCard {

    var trashingCard: Boolean = false

    var discardingCard: Boolean = false

    var topDeckCards = mutableListOf<Card>()

    init {
        addActions = 1
        special = "Look at the top 3 cards of your deck. Trash one of them. Discard one of them. Put the other one back on to your deck."
        textSize = 95
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(3)

        topDeckCards = cards.toMutableList()

        if (cards.isNotEmpty()) {
            if (cards.size == 1) {
                player.cardTrashed(cards.first())
                player.addUsernameGameLog(" trashed ${cards.first().cardNameWithBackgroundColor}")
            } else {
                trashingCard = true
                player.chooseCardAction("Choose a card to trash", this, cards, false)
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        if (trashingCard) {
            trashingCard = false

            player.cardTrashed(card)
            player.addUsernameGameLog(" trashed ${card.cardNameWithBackgroundColor}")

            topDeckCards.remove(card)

            if (topDeckCards.isNotEmpty()) {
                if (topDeckCards.size == 1) {
                    player.addCardToDiscard(card)
                    player.addUsernameGameLog(" discarded ${card.cardNameWithBackgroundColor}")
                } else {
                    discardingCard = true
                    player.chooseCardAction("Choose a card to discard", this, topDeckCards, false)
                }
            }
        } else if (discardingCard) {
            discardingCard = false

            player.addCardToDiscard(card)
            player.addUsernameGameLog(" discarded ${card.cardNameWithBackgroundColor}")

            topDeckCards.remove(card)

            if (topDeckCards.isNotEmpty()) {
                player.addCardToTopOfDeck(topDeckCards.first(), false)
            }
        }
    }

    companion object {
        const val NAME: String = "Lookout"
    }
}

