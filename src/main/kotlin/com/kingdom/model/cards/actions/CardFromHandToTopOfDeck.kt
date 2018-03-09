package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class CardFromHandToTopOfDeck(text: String) : Action(text) {

    override var isShowDoNotUse: Boolean = true

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: OldPlayer): Boolean {
        return cardLocation == CardLocation.Hand
    }

    override fun processAction(player: OldPlayer): Boolean {
        return !player.hand.isEmpty()
    }

    override fun processActionResult(player: OldPlayer, result: ActionResult): Boolean {
        val card = result.selectedCard!!
        player.hand.remove(card)
        player.addCardToTopOfDeck(card)
        return true
    }
}