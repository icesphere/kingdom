package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Save : AdventuresEvent(NAME, 1, true), ChooseCardActionCard {

    init {
        special = "Once per turn: +1 Buy. Set aside a card from your hand, and put it into your hand at end of turn (after drawing)."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.hand.isNotEmpty()
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addBuys(1)

        player.chooseCardFromHand("Choose a card to set aside from your hand, that will be put it into your hand at end of turn (after drawing).", this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardFromHand(card)
        player.cardToPutIntoHandAfterDrawingCardsAtEndOfTurn = card
    }

    companion object {
        const val NAME: String = "Save"
    }
}