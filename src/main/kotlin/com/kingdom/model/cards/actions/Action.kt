package com.kingdom.model.cards.actions

import com.kingdom.model.Choice
import com.kingdom.model.Player
import com.kingdom.model.cards.Card

abstract class Action(open var text: String?) {

    var choices: List<Choice>? = null

    abstract fun isCardActionable(card: Card, cardLocation: String, player: Player): Boolean

    abstract fun processAction(player: Player): Boolean

    abstract fun processActionResult(player: Player, result: ActionResult): Boolean

    open val isShowDoNotUse: Boolean = false

    val isShowDone: Boolean = false

    val doneText: String = "Done"

    fun isCardSelected(card: Card): Boolean = false

    fun onNotUsed(player: Player) {}
}