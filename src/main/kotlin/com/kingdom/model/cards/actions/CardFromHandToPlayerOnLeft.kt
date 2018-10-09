package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class CardFromHandToPlayerOnLeft(val playerToLeft: Player) : Action("Choose a card from your hand to pass to ${playerToLeft.username}") {

    override var isShowDoNotUse: Boolean = false

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Hand
    }

    override fun processAction(player: Player): Boolean {
        return player.hand.isNotEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!
        player.hand.remove(card)
        playerToLeft.acquireCardToHand(card)
        return true
    }
}