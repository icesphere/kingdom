package com.kingdom.model.cards.actions

import com.kingdom.model.Choice
import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

abstract class Action(open var text: String?) {

    open var choices: List<Choice>? = null

    abstract fun isCardActionable(card: Card, cardLocation: CardLocation, player: OldPlayer): Boolean

    abstract fun processAction(player: OldPlayer): Boolean

    abstract fun processActionResult(player: OldPlayer, result: ActionResult): Boolean

    open var isShowDoNotUse: Boolean = false

    open val isShowDone: Boolean = false

    open val doneText: String = "Done"

    open fun isCardSelected(card: Card): Boolean = false

    open fun onNotUsed(player: OldPlayer) {}
}