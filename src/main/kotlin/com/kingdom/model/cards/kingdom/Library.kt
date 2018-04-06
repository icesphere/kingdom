package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Library : KingdomCard(NAME, CardType.Action, 5), ChoiceActionCard {

    val skippedActionCards: MutableList<Card> = ArrayList()

    lateinit var cardToAdd: Card

    lateinit var player: Player

    init {
        special = "Draw until you have 7 cards in hand, skipping any Action cards you choose to; set those aside, discarding them afterwards."
        textSize = 95
    }

    override fun cardPlayedSpecialAction(player: Player) {
        this.player = player
        drawCardsForLibrary()
    }

    private fun drawCardsForLibrary() {
        if (player.hand.size < 7) {
            val card = player.removeTopCardOfDeck()
            if (card != null) {
                if (card.isAction) {
                    cardToAdd = card
                    player.yesNoChoice(this, "Add ${card.cardNameWithBackgroundColor} to your hand?")
                }
            }
        } else {
            player.discard.addAll(skippedActionCards)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            skippedActionCards.add(cardToAdd)
        } else {
            player.hand.add(cardToAdd)
        }

        drawCardsForLibrary()
    }

    companion object {
        const val NAME: String = "Library"
    }
}

