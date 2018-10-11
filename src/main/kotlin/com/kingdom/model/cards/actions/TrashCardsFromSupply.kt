package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player
import java.util.*

class TrashCardsFromSupply(private var numCardsToScrap: Int, val optional: Boolean, private val expression: ((card: Card) -> Boolean)? = null) : Action("") {

    private var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() =
            selectedCards.size in 1..numCardsToScrap && (this.isShowDoNotUse || selectedCards.size == numCardsToScrap)

    override val doneText: String
        get() = if (selectedCards.size == 1) {
            "Trash " + selectedCards[0].name
        } else {
            "Trash " + selectedCards.size + " cards"
        }

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
        text += numCardsToScrap.toString() + " card"
        if (numCardsToScrap != 1) {
            text += "s"
        }
        text += " from the supply"
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Supply && (expression == null || expression.invoke(card))
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
                if (numCardsToScrap == 1) {
                    applyActionToSelectedCards(player)
                    return true
                }
                selectedCards.add(selectedCard)
            }
        }

        return false
    }

    private fun applyActionToSelectedCards(player: Player) {
        selectedCards.forEach { c -> player.game.trashCardFromSupply(c) }
    }

    override fun isCardSelected(card: Card): Boolean {
        return selectedCards.contains(card)
    }
}