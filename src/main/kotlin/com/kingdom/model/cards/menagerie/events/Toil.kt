package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Toil : MenagerieEvent(NAME, 2), ChooseCardActionCard {

    init {
        addBuys = 1
        special = "You may play an Action card from your hand."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.hand.any { it.isAction }
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand("Choose Action card to play", this) { it.isAction }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addActions(1)
        player.playCard(card)
    }

    companion object {
        const val NAME: String = "Toil"
    }
}