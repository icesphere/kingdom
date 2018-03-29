package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class CardFromHandToTopOfDeck() : Action("Choose a card from your hand to put on top of your deck") {

    override var isShowDoNotUse: Boolean = true

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Hand
    }

    override fun processAction(player: Player): Boolean {
        return !player.hand.isEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!
        player.hand.remove(card)
        player.addCardToTopOfDeck(card)
        return true
    }
}