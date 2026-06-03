package com.kingdom.model.cards.actions

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class UndoApprovalAction(actorUsername: String, summary: String) :
        Action("$actorUsername wants to undo: $summary") {

    override var choices: List<Choice>? = listOf(Choice(1, "Allow undo"), Choice(2, "Do not allow"))

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean = false

    override fun processAction(player: Player): Boolean = true

    override fun processActionResult(player: Player, result: ActionResult): Boolean = false
}

class UndoWaitingAction(summary: String) : Action("Waiting for other players to approve undo: $summary") {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean = false

    override fun processAction(player: Player): Boolean = true

    override fun processActionResult(player: Player, result: ActionResult): Boolean = false
}
