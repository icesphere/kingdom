package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import java.util.*

class TrashCardsFromDiscardPile : Action, SelectFromDiscardAction {
    protected var numCardsToScrap: Int = 0

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() = selectedCards.size > 0 && (this.isShowDoNotUse || selectedCards.size == numCardsToScrap)

    override val doneText: String
        get() = if (selectedCards.size == 1) {
            "Scrap " + selectedCards[0].name
        } else {
            "Scrap " + selectedCards.size + " cards"
        }

    constructor(numCardsToScrap: Int) : super("") {
        this.numCardsToScrap = numCardsToScrap
        text = "Scrap $numCardsToScrap card"
        if (numCardsToScrap != 1) {
            text += "s"
        }
    }

    constructor(numCardsToScrap: Int, text: String) : super(text) {
        this.numCardsToScrap = numCardsToScrap
    }

    constructor(numCardsToScrap: Int, text: String, optional: Boolean) : super(text) {
        this.numCardsToScrap = numCardsToScrap
        this.isShowDoNotUse = optional
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Discard
    }

    override fun processAction(player: Player): Boolean {
        return player.cardsInDiscard.isNotEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            selectedCards.forEach( { player.trashCardFromDiscard(it) })
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