package com.kingdom.model.cards.actions

import com.kingdom.model.Choice
import com.kingdom.model.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

abstract class Action(open var text: String?) {

    open var choices: List<Choice>? = null

    abstract fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean

    abstract fun processAction(player: Player): Boolean

    abstract fun processActionResult(player: Player, result: ActionResult): Boolean

    open var isShowDoNotUse: Boolean = false

    open val isShowDone: Boolean = false

    open val doneText: String = "Done"

    open fun isCardSelected(card: Card): Boolean = false

    open fun onNotUsed(player: Player) {}
}