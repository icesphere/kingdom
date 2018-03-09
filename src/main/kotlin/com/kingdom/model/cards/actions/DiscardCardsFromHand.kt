package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import java.util.*

open class DiscardCardsFromHand : Action {
    var numCardsToDiscard: Int = 0

    protected var selectedCards: MutableList<Card> = ArrayList()

    override//todo handle if not optional and available cards < numCardsToDiscard
    val isShowDone: Boolean
        get() =
            selectedCards.size > 0 && selectedCards.size <= numCardsToDiscard && (this.isShowDoNotUse || selectedCards.size == numCardsToDiscard)

    override val doneText: String
        get() = if (selectedCards.size == 1) {
            "Discard " + selectedCards[0].name
        } else {
            "Discard " + selectedCards.size + " cards"
        }

    constructor(numCardsToDiscard: Int) : super("") {
        this.numCardsToDiscard = numCardsToDiscard
        setTextFromNumberOfCardsToDiscard()
    }

    private fun setTextFromNumberOfCardsToDiscard() {
        text = "Discard $numCardsToDiscard card"
        if (numCardsToDiscard != 1) {
            text += "s"
        }
    }

    constructor(numCardsToDiscard: Int, text: String) : super(text) {
        this.numCardsToDiscard = numCardsToDiscard
    }

    constructor(numCardsToDiscard: Int, text: String, optional: Boolean) : super(text) {
        this.numCardsToDiscard = numCardsToDiscard
        this.isShowDoNotUse = optional
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: OldPlayer): Boolean {
        return cardLocation == CardLocation.Hand
    }

    override fun processAction(player: OldPlayer): Boolean {
        return !player.hand.isEmpty()
    }

    override fun processActionResult(player: OldPlayer, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            selectedCards.forEach( { player.discardCardFromHand(it) })
            return true
        } else {
            val selectedCard = result.selectedCard!!
            if (selectedCards.contains(selectedCard)) {
                selectedCards.remove(selectedCard)
            } else {
                if (numCardsToDiscard == 1) {
                    selectedCards.clear()
                }
                selectedCards.add(selectedCard)
            }
        }

        return false
    }

    override fun isCardSelected(card: Card): Boolean {
        return selectedCards.contains(card)
    }
}