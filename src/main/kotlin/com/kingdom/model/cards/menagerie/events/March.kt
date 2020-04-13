package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class March : MenagerieEvent(NAME, 3), ChooseCardActionCard {

    init {
        special = "Look through your discard pile. You may play an Action card from it."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.cardsInDiscard.count { it.isAction } > 0
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cardsToSelectFrom = player.cardsInDiscardCopy.filter { it.isAction }
        player.chooseCardAction("Choose an Action card to play from your discard pile", this, cardsToSelectFrom, true)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardFromDiscard(card)
        player.addActions(1)
        player.playCard(card)
    }

    companion object {
        const val NAME: String = "March"
    }
}