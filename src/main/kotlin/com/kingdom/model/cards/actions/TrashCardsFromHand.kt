package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player
import java.util.*

open class TrashCardsFromHand(private var numCardsToTrash: Int, text: String, optional: Boolean, private val cardActionableExpression: ((card: Card) -> Boolean)? = null) : Action(text) {

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() =
            numCardsToTrash > 1 && ((this.isShowDoNotUse && selectedCards.size <= numCardsToTrash) || selectedCards.size == numCardsToTrash)

    init {
        isShowDoNotUse = optional

        if (this.text == "") {
            if (optional) {
                this.text = "You may trash"
                if (numCardsToTrash > 1) {
                    this.text += " up to"
                }
            } else {
                this.text = "Trash"
            }
            this.text += " $numCardsToTrash card"
            if (numCardsToTrash != 1) {
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
        if (actionableCards.size < numCardsToTrash) {
            numCardsToTrash = actionableCards.size
        }
        return actionableCards.isNotEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            selectedCards.forEach { player.trashCardFromHand(it) }
            return true
        } else {
            val selectedCard = result.selectedCard!!

            if (numCardsToTrash == 1) {
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