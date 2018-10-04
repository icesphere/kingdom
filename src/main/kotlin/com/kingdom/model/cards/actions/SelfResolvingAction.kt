package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

abstract class SelfResolvingAction : Action("") {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean = false

    override fun processAction(player: Player): Boolean = true

    override fun processActionResult(player: Player, result: ActionResult): Boolean = true

    abstract fun resolveAction(player: Player)
}