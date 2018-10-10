package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import java.util.*

open class DiscardCardsFromHand(private var numCardsToDiscard: Int, text: String, optional: Boolean) : Action(text) {

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() =
            numCardsToDiscard > 1 && (this.isShowDoNotUse || selectedCards.size == numCardsToDiscard)

    override val doneText: String
        get() = if (selectedCards.size == 1) {
            "Discard " + selectedCards[0].name
        } else {
            "Discard " + selectedCards.size + " cards"
        }

    init {
        isShowDoNotUse = optional

        if (this.text == "") {
            this.text = "Discard $numCardsToDiscard card"
            if (numCardsToDiscard != 1) {
                this.text += "s"
            }
            this.text += " from your hand"
        }
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Hand
    }

    override fun processAction(player: Player): Boolean {
        if (player.hand.size < numCardsToDiscard) {
            numCardsToDiscard = player.hand.size
        }
        return !player.hand.isEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            selectedCards.forEach({ player.discardCardFromHand(it) })
            return true
        } else {
            val selectedCard = result.selectedCard!!

            if (numCardsToDiscard == 1) {
                selectedCards.add(selectedCard)
                player.discardCardFromHand(selectedCard)
                return true
            }

            if (selectedCards.contains(selectedCard)) {
                selectedCards.remove(selectedCard)
            } else {
                selectedCards.add(selectedCard)
            }
        }

        return false
    }

    override fun isCardSelected(card: Card): Boolean {
        return selectedCards.contains(card)
    }
}