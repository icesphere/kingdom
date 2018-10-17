package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class ChooseCardFromHand(private val chooseCardActionCard: ChooseCardActionCard, text: String) : Action(text) {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Hand
    }

    override fun processAction(player: Player): Boolean {
        return player.hand.isNotEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        chooseCardActionCard.onCardChosen(player, result.selectedCard!!)
        return true
    }
}