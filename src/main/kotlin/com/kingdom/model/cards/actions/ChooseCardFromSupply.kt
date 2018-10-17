package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class ChooseCardFromSupply(private val chooseCardActionCard: ChooseCardActionCard, text: String) : Action(text) {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Supply && player.game.isCardAvailableInSupply(card)
    }

    override fun processAction(player: Player): Boolean = true

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        chooseCardActionCard.onCardChosen(player, result.selectedCard!!)
        return true
    }
}