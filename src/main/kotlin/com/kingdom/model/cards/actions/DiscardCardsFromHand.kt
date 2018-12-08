package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import java.util.*

open class DiscardCardsFromHand(private var numCardsToDiscard: Int, text: String, optional: Boolean, private val cardActionableExpression: ((card: Card) -> Boolean)? = null) : Action(text) {

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() = numCardsToDiscard > 1 && ((this.isShowDoNotUse && selectedCards.size <= numCardsToDiscard) || selectedCards.size == numCardsToDiscard)

    init {
        isShowDoNotUse = optional

        if (this.text == "") {
            this.text = "Discard "
            if (optional) {
                this.text += "up to "
            }
            this.text += "$numCardsToDiscard card"
            if (numCardsToDiscard != 1) {
                this.text += "s"
            }
            this.text += " from your hand"
        }
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Hand && (cardActionableExpression == null || cardActionableExpression.invoke(card))
    }

    override fun processAction(player: Player): Boolean {
        val actionableCards = player.hand.filter { cardActionableExpression == null || cardActionableExpression.invoke(it) }
        if (actionableCards.size < numCardsToDiscard) {
            numCardsToDiscard = actionableCards.size
        }
        return actionableCards.isNotEmpty()
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