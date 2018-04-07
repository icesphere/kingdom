package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import java.util.*

class DrawCardsAndPutSomeBackOnTopOfDeck(private var numCardsToDraw: Int, private var numCardsToPutBack: Int) : Action("") {

    protected var cardsDrawn: List<Card> = ArrayList()

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() = selectedCards.size in 1..numCardsToPutBack && selectedCards.size == numCardsToPutBack

    init {
        this.text = "You have drawn $numCardsToDraw cards and need to put $numCardsToPutBack of those cards back on top of your deck"
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Hand && cardsDrawn.contains(card)
    }

    override fun processAction(player: Player): Boolean {
        cardsDrawn = player.drawCards(numCardsToDraw)
        if (cardsDrawn.size < 3) {
            cardsDrawn.forEach { c ->
                player.hand.remove(c)
                player.addCardToTopOfDeck(c)
            }
            return false
        } else {
            return true
        }
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            selectedCards.forEach { c ->
                player.hand.remove(c)
                player.addCardToTopOfDeck(c)
            }
            return true
        } else {
            val selectedCard = result.selectedCard!!
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