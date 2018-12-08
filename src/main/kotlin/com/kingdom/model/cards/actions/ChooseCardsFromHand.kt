package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import java.util.*

open class ChooseCardsFromHand(private var numCardsToChoose: Int, text: String, optional: Boolean, private val chooseCardsActionCard: ChooseCardsActionCard, private val cardActionableExpression: ((card: Card) -> Boolean)? = null, private val info: Any? = null) : Action(text) {

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() = numCardsToChoose > 1 && ((this.isShowDoNotUse && selectedCards.size <= numCardsToChoose) || selectedCards.size == numCardsToChoose)

    init {
        isShowDoNotUse = optional

        if (this.text == "") {
            this.text = "Choose "
            if (optional) {
                this.text += "up to "
            }
            this.text += "$numCardsToChoose card"
            if (numCardsToChoose != 1) {
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
        if (actionableCards.size < numCardsToChoose) {
            numCardsToChoose = actionableCards.size
        }
        return actionableCards.isNotEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            chooseCardsActionCard.onCardsChosen(player, selectedCards, info)
            return true
        } else {
            val selectedCard = result.selectedCard!!

            if (numCardsToChoose == 1) {
                selectedCards.add(selectedCard)
                chooseCardsActionCard.onCardsChosen(player, selectedCards, info)
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