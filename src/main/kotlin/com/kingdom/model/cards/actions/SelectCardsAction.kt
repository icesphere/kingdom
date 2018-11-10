package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player
import java.util.*

abstract class SelectCardsAction(text: String,
                                 private var numCardsToSelect: Int = 1,
                                 optional: Boolean = false,
                                 private val cardActionableExpression: ((card: Card) -> Boolean)? = null) : Action(text) {

    override var isShowDoNotUse: Boolean = optional

    override val isShowDone: Boolean
        get() = numCardsToSelect > 1 && selectedCards.isNotEmpty() && (this.isShowDoNotUse || selectedCards.size == numCardsToSelect)

    protected var selectedCards: MutableList<Card> = ArrayList()

    abstract val cardsToSelectFrom: List<Card>

    abstract val selectFromLocation: CardLocation

    override fun isCardActionable(card: Card,
                                  cardLocation: CardLocation,
                                  player: Player): Boolean = cardLocation == this.selectFromLocation && (cardActionableExpression == null || cardActionableExpression.invoke(card))

    override fun processAction(player: Player): Boolean {
        val actionableCards = cardsToSelectFrom.filter { cardActionableExpression == null || cardActionableExpression.invoke(it) }
        if (actionableCards.size < numCardsToSelect) {
            numCardsToSelect = actionableCards.size
        }
        return actionableCards.isNotEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            onSelectionDone(player)
            return true
        } else {
            val selectedCard = result.selectedCard!!

            if (numCardsToSelect == 1) {
                selectedCards.add(selectedCard)
                onSelectionDone(player)
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

    abstract fun onSelectionDone(player: Player)

    override fun isCardSelected(card: Card): Boolean {
        return selectedCards.contains(card)
    }
}