package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import java.util.*

class TrashCardsFromSupply(protected var numCardsToScrap: Int, optional: Boolean) : Action("") {

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() =
            selectedCards.size > 0 && selectedCards.size <= numCardsToScrap && (this.isShowDoNotUse || selectedCards.size == numCardsToScrap)

    override val doneText: String
        get() = if (selectedCards.size == 1) {
            "Scrap " + selectedCards[0].name
        } else {
            "Scrap " + selectedCards.size + " cards"
        }

    private fun setTextFromNumCards() {
        text = "Scrap "
        if (this.isShowDoNotUse) {
            text += "up to "
        }
        text += numCardsToScrap.toString() + " card"
        if (numCardsToScrap != 1) {
            text += "s"
        }
        text += " from the trade row"
    }

    init {
        this.isShowDoNotUse = optional
        setTextFromNumCards()
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: OldPlayer): Boolean {
        return cardLocation == CardLocation.Supply
    }

    override fun processAction(player: OldPlayer): Boolean {
        return !player.game.supply.isEmpty()
    }

    override fun processActionResult(player: OldPlayer, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            selectedCards.forEach { c -> player.game.trashCardFromSupply(c) }
            return true
        } else {
            val selectedCard = result.selectedCard!!
            if (selectedCards.contains(selectedCard)) {
                selectedCards.remove(selectedCard)
            } else {
                if (numCardsToScrap == 1) {
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