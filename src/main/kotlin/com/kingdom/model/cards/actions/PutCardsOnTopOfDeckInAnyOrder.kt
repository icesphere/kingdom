package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import java.util.*

class PutCardsOnTopOfDeckInAnyOrder(private val cards: List<Card>) : Action("Select cards in the order you want them to go on top of your deck") {

    protected var selectedCards: MutableList<Card> = ArrayList()

    override val isShowDone: Boolean
        get() = selectedCards.size > 0 && selectedCards.size == cards.size

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean = false

    override fun processAction(player: Player): Boolean = true

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (result.isDoneWithAction) {
            selectedCards.forEach { c ->
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