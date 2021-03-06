package com.kingdom.model.cards.intrigue

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Courtier : IntrigueCard(NAME, CardType.Action, 4), ChoiceActionCard, ChooseCardActionCard {

    private val choices = listOf(
            Choice(1, "+1 Action"),
            Choice(2, "+1 Buy"),
            Choice(3, "+\$3"),
            Choice(4, "Gain a Gold")
    )

    var choicesChosen = mutableListOf<Int>()

    var numChoices = 0

    init {
        isAddCoinsCard = true
        special = "Reveal a card from your hand. For each type it has (Action, Attack, etc.), choose one: +1 Action; or +1 Buy; or +\$3; or gain a Gold. The choices must be different."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand(special, this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addEventLogWithUsername("revealed ${card.cardNameWithBackgroundColor}")

        numChoices = card.numTypes

        player.makeChoiceFromList(this, choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        choicesChosen.add(choice)

        when (choice) {
            1 -> {
                player.addEventLogWithUsername("chose +1 Action")
                player.addActions(1)
            }
            2 -> {
                player.addEventLogWithUsername("chose +1 Buy")
                player.addBuys(1)
            }
            3 -> {
                player.addEventLogWithUsername("chose +\$3")
                player.addCoins(3)
            }
            4 -> {
                player.addEventLogWithUsername("chose to gain a Gold")
                player.gainSupplyCard(Gold())
            }
        }

        if (choicesChosen.size < numChoices) {
            val availableChoices = choices.filterNot { choicesChosen.contains(it.choiceNumber) }

            if (availableChoices.isNotEmpty()) {
                player.makeChoice(this, *availableChoices.toTypedArray())
            }
        }
    }

    override fun removedFromPlay(player: Player) {
        choicesChosen.clear()
        super.removedFromPlay(player)
    }

    companion object {
        const val NAME: String = "Courtier"
    }
}

