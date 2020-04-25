package com.kingdom.model.cards.menagerie

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Scrap : MenagerieCard(NAME, CardType.Action, 3), TrashCardsForBenefitActionCard, UsesHorses, ChoiceActionCard {

    init {
        special = "Trash a card from your hand. Choose a different thing per \$ it costs: +1 Card; +1 Action; +1 Buy; +\$1 ; gain a Silver; gain a Horse."
    }

    private val choices = listOf(
            Choice(1, "+1 Card"),
            Choice(2, "+1 Action"),
            Choice(3, "+1 Buy"),
            Choice(4, "+\$1"),
            Choice(5, "Gain a Silver"),
            Choice(6, "Gain a Horse")
    )

    var choicesChosen = mutableListOf<Int>()

    var numChoices = 0

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.trashCardsFromHandForBenefit(this, 1, special)
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        numChoices = player.getCardCostWithModifiers(trashedCards.first())

        if (numChoices > 0) {
            player.makeChoiceFromList(this, choices)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        choicesChosen.add(choice)

        when (choice) {
            1 -> {
                player.addEventLogWithUsername("chose +1 Card")
                player.drawCard()
            }
            2 -> {
                player.addEventLogWithUsername("chose +1 Action")
                player.addActions(1)
            }
            3 -> {
                player.addEventLogWithUsername("chose +1 Buy")
                player.addBuys(1)
            }
            4 -> {
                player.addEventLogWithUsername("chose +\$1")
                player.addCoins(1)
            }
            5 -> {
                player.addEventLogWithUsername("chose to gain a Silver")
                player.gainSupplyCard(Silver())
            }
            6 -> {
                player.addEventLogWithUsername("chose to gain a Horse")
                player.gainHorse()
            }
        }

        if (choicesChosen.size < numChoices) {
            val availableChoices = choices.filterNot { choicesChosen.contains(it.choiceNumber) }
            if (availableChoices.isNotEmpty()) {
                player.makeChoice(this, *availableChoices.toTypedArray())
            }
        }
    }

    companion object {
        const val NAME: String = "Scrap"
    }
}

