package com.kingdom.model.cards.actions

import com.kingdom.model.Choice
import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

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

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: OldPlayer): Boolean = false

    override fun processAction(player: OldPlayer): Boolean = true

    override fun processActionResult(player: OldPlayer, result: ActionResult): Boolean {
        card!!.actionChoiceMade(player, result.choiceSelected!!)
        return true
    }
}