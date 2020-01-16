package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class ShowTavernCardsAction : Action("Tavern cards - if a card is highlighted you can click on it to call it from your Tavern mat") {

    override var isShowDoNotUse: Boolean = true

    override val isShowDone: Boolean = false

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.CardAction && card is TavernCard && card.isTavernCardActionable(player)
    }

    override fun processAction(player: Player): Boolean {
        cardChoices = player.tavernCards

        return player.tavernCards.isNotEmpty()
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (!result.isDoNotUse && result.selectedCard != null) {
            player.callTavernCard(result.selectedCard!!)
        }

        return true
    }
}