package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class RepeatCardAction(private val repeatedCard: Card) : SelfResolvingAction() {

    override fun resolveAction(player: Player) {
        player.addActions(1)
        player.playCard(repeatedCard, repeatedAction = true)
    }
}