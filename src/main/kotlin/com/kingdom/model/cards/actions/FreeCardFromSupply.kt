package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class FreeCardFromSupply(val maxCost: Int?, text: String, val destination: CardLocation = CardLocation.Discard) : Action(text) {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: OldPlayer): Boolean {
        return ((cardLocation == CardLocation.Supply)
                && (maxCost == null || player.game.getCardCost(card) <= maxCost))
    }

    override fun processAction(player: OldPlayer): Boolean {
        if (player.game.supply.isEmpty()) {
            return false
        } else {
            player.game.addHistory(player.username + " is choosing a free card from the trade row")
            return true
        }
    }

    override fun processActionResult(player: OldPlayer, result: ActionResult): Boolean {
        val card = result.selectedCard!!

        player.game.addHistory(player.username + " acquired a free card from the trade row: " + card.name)

        player.game.playerGainedCard(player, card, destination, true, false)

        return true
    }
}