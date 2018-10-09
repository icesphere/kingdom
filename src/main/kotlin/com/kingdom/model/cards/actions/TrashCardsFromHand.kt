package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player
import java.util.*

open class TrashCardsFromHand(private var numCardsToScrap: Int, text: String, optional: Boolean) : Action(text) {

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() =
            numCardsToScrap > 1 && selectedCards.size in 1..numCardsToScrap && (this.isShowDoNotUse || selectedCards.size == numCardsToScrap)

    override val doneText: String
        get() = if (selectedCards.size == 1) {
            "Trash " + selectedCards[0].name
        } else {
            "Trash " + selectedCards.size + " cards"
        }

    init {
        isShowDoNotUse = optional

        if (this.text == "") {
            this.text = "Trash $numCardsToScrap card"
            if (numCardsToScrap != 1) {
                this.text += "s"
            }
            this.text += " from your hand"
        }
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Hand
    }

    override fun processAction(player: Player): Boolean {
        if (player.hand.size < numCardsToScrap) {
            numCardsToScrap = player.hand.size
        }
        return !player.hand.isEmpty()
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