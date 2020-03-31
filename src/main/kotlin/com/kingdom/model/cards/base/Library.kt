package com.kingdom.model.cards.base

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Library : BaseCard(NAME, CardType.Action, 5), ChoiceActionCard {

    private val skippedActionCards: MutableList<Card> = ArrayList()

    lateinit var cardToAdd: Card

    lateinit var player: Player

    init {
        special = "Draw until you have 7 cards in hand, skipping any Action cards you choose to; set those aside, discarding them afterwards."
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
                } else {
                    player.addCardToHand(card, true)
                    drawCardsForLibrary()
                }
            } else {
                player.addCardsToDiscard(skippedActionCards, true)
            }
        } else {
            player.addCardsToDiscard(skippedActionCards, true)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addCardToHand(cardToAdd)
        } else {
            skippedActionCards.add(cardToAdd)
        }

        drawCardsForLibrary()
    }

    companion object {
        const val NAME: String = "Library"
    }
}

