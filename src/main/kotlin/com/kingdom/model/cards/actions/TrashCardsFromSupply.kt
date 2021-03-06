package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player
import java.util.*

class TrashCardsFromSupply(private var numCardsToTrash: Int, val optional: Boolean, private val cardActionableExpression: ((card: Card) -> Boolean)? = null) : Action("") {

    private var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() =
            numCardsToTrash > 1 && ((this.isShowDoNotUse && selectedCards.size <= numCardsToTrash) || selectedCards.size == numCardsToTrash)

    init {
        this.isShowDoNotUse = optional
        setTextFromNumCards()
    }

    private fun setTextFromNumCards() {
        text = if (optional) {
            "You may trash "
        } else {
            "Trash "
        }
        if (this.isShowDoNotUse) {
            text += "up to "
        }
        text += numCardsToTrash.toString() + " card"
        if (numCardsToTrash != 1) {
            text += "s"
        }
        text += " from the supply"
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Supply && (cardActionableExpression == null || cardActionableExpression.invoke(card))
    }

    override fun processAction(player: Player): Boolean {
        return true
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            applyActionToSelectedCards(player)
            return true
        } else {
            val selectedCard = result.selectedCard!!
            if (selectedCards.contains(selectedCard)) {
                selectedCards.remove(selectedCard)
            } else {
                selectedCards.add(selectedCard)

                if (numCardsToTrash == 1) {
                    applyActionToSelectedCards(player)
                    return true
                }
            }
        }

        return false
    }

    private fun applyActionToSelectedCards(player: Player) {
        selectedCards.forEach { c -> player.trashCardFromSupply(c) }
    }

    override fun isCardSelected(card: Card): Boolean {
        return selectedCards.contains(card)
    }
}