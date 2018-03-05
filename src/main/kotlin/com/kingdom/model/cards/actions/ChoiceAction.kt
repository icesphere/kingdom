package com.kingdom.model.cards.actions

import com.kingdom.model.Choice
import com.kingdom.model.Player
import com.kingdom.model.cards.Card

class ChoiceAction : Action {
    private var card: ChoiceActionCard? = null

    constructor(card: ChoiceActionCard, vararg choices: Choice) : super("") {
        this.card = card
        this.choices = choices.toList()
    }

    constructor(card: ChoiceActionCard, text: String, vararg choices: Choice): super(text) {
        this.card = card
        this.choices = choices.toList()
    }

    override fun isCardActionable(card: Card, cardLocation: String, player: Player): Boolean = false

    override fun processAction(player: Player): Boolean = true

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        card!!.actionChoiceMade(player, result.choiceSelected!!)
        return true
    }
}