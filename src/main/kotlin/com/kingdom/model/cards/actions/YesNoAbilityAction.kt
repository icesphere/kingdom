package com.kingdom.model.cards.actions

import com.kingdom.model.Choice
import com.kingdom.model.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import java.util.ArrayList

class YesNoAbilityAction(private val card: ChoiceActionCard, text: String) : Action(text) {

    override var choices: List<Choice>?
        get() {
            val choices: MutableList<Choice> = ArrayList()

            val yes = Choice(1, "Yes")
            val no = Choice(2, "No")

            choices.add(yes)
            choices.add(no)

            return choices
        }
        set(value) {
            super.choices = value
        }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean = false

    override fun processAction(player: Player): Boolean = true

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        card.actionChoiceMade(player, result.choiceSelected!!)
        return true
    }
}