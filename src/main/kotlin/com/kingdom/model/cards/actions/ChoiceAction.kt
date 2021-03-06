package com.kingdom.model.cards.actions

import com.kingdom.model.Choice
import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class ChoiceAction : Action {
    private var card: ChoiceActionCard? = null

    private var info: Any? = null

    constructor(card: ChoiceActionCard, vararg choices: Choice) : super("") {
        this.card = card
        this.choices = choices.toList()
    }

    constructor(card: ChoiceActionCard, text: String, vararg choices: Choice): super(text) {
        this.card = card
        this.choices = choices.toList()
    }

    constructor(card: ChoiceActionCard, text: String, info: Any, vararg choices: Choice): super(text) {
        this.card = card
        this.choices = choices.toList()
        this.info = info
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean = false

    override fun processAction(player: Player): Boolean = true

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        card!!.actionChoiceMade(player, result.choiceSelected!!, info)
        return true
    }
}