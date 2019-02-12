package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Lookout : SeasideCard(NAME, CardType.Action, 3), ChooseCardActionCard {

    var choosingCardToTrash: Boolean = false

    var choosingCardToDiscard: Boolean = false

    init {
        addActions = 1
        special = "Look at the top 3 cards of your deck. Trash one of them. Discard one of them. Put the other one back on to your deck."
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(3)

        val topDeckCards = cards.toMutableList()

        if (cards.isNotEmpty()) {
            if (cards.size == 1) {
                player.cardTrashed(cards.first())
                player.addEventLogWithUsername(" trashed ${cards.first().cardNameWithBackgroundColor}")
            } else {
                choosingCardToTrash = true
                player.chooseCardAction("Choose a card to trash", this, cards, false, topDeckCards)
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {

        @Suppress("UNCHECKED_CAST")
        val topDeckCards = info as MutableList<Card>

        if (choosingCardToTrash) {
            choosingCardToTrash = false

            player.cardTrashed(card)
            player.addEventLogWithUsername(" trashed ${card.cardNameWithBackgroundColor}")

            topDeckCards.remove(card)

            if (topDeckCards.isNotEmpty()) {
                if (topDeckCards.size == 1) {
                    player.addCardToDiscard(card)
                    player.addEventLogWithUsername(" discarded ${card.cardNameWithBackgroundColor}")
                } else {
                    choosingCardToDiscard = true
                    player.chooseCardAction("Choose a card to discard", this, topDeckCards, false, topDeckCards)
                }
            }
        } else if (choosingCardToDiscard) {
            choosingCardToDiscard = false

            player.addCardToDiscard(card)
            player.addEventLogWithUsername(" discarded ${card.cardNameWithBackgroundColor}")

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

