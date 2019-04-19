package com.kingdom.model.cards.adventures

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Amulet : AdventuresCard(NAME, CardType.ActionDuration, 3), StartOfTurnDurationAction, ChoiceActionCard {

    init {
        special = "Now and at the start of your next turn, choose one: +\$1; or trash a card from your hand; or gain a Silver."
        isTrashingCard = true
        isAddCoinsCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        handleChoice(player)
    }

    override fun durationStartOfTurnAction(player: Player) {
        handleChoice(player)
    }

    private fun handleChoice(player: Player) {
        player.makeChoice(this, Choice(1, "+\$1"), Choice(2, "Trash a card"), Choice(3, "Gain Silver"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> {
                player.addEventLogWithUsername("Chose +\$1")
                player.addCoins(1)
            }
            2 -> {
                player.addEventLogWithUsername("Chose to trash a card")
                player.trashCardFromHand(false)
            }
            3 -> {
                player.addEventLogWithUsername("Chose to gain a Silver")
                player.gainSupplyCard(Silver())
            }
        }
    }

    companion object {
        const val NAME: String = "Amulet"
    }
}

