package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player
import java.util.*

open class TrashCardsFromHand(private var numCardsToScrap: Int, text: String, optional: Boolean, private val cardActionableExpression: ((card: Card) -> Boolean)? = null) : Action(text) {

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() =
            numCardsToScrap > 1 && (this.isShowDoNotUse || selectedCards.size == numCardsToScrap)

    init {
        isShowDoNotUse = optional

        if (this.text == "") {
            if (optional) {
                this.text = "You may trash"
                if (numCardsToScrap > 1) {
                    this.text += " up to"
                }
            } else {
                this.text = "Trash"
            }
            this.text += " $numCardsToScrap card"
            if (numCardsToScrap != 1) {
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
        if (actionableCards.size < numCardsToScrap) {
            numCardsToScrap = actionableCards.size
        }
        return actionableCards.isNotEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            selectedCards.forEach({ player.trashCardFromHand(it) })
            return true
        } else {
            val selectedCard = result.selectedCard!!

            if (numCardsToScrap == 1) {
                selectedCards.add(selectedCard)
                player.trashCardFromHand(selectedCard)
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