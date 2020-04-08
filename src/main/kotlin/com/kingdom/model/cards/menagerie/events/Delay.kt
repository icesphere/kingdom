package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Delay : MenagerieEvent(NAME, 0), ChooseCardActionCard {

    init {
        special = "You may set aside an Action card from your hand. At the start of your next turn, play it."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.hand.any { it.isAction }
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand("Choose a card to set aside from your hand, that will be played at the start of your next turn.", this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardFromHand(card)
        player.cardsToPlayAtStartOfNextTurn.add(card)
    }

    companion object {
        const val NAME: String = "Delay"
    }
}