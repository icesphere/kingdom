package com.kingdom.model.cards.menagerie

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.ConditionalDuration
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class Barge : MenagerieCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction, ConditionalDuration, ChoiceActionCard {

    private val actionChoices = mutableListOf<Int>()

    override val isKeepAtEndOfTurn: Boolean
        get() = actionChoices.any { it == 2 }

    init {
        special = "Either now or at the start of your next turn, +3 Cards and +1 Buy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, "+3 Cards and +1 Buy", Choice(1, "Now"), Choice(2, "Next Turn"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        actionChoices.add(choice)

        if (choice == 1) {
            player.drawCards(3)
            player.addBuys(1)
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        actionChoices.forEach {
            if (it == 2) {
                player.drawCards(3)
                player.addBuys(1)
            }
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        actionChoices.clear()
    }

    override fun beforeCardRepeated(player: Player) {
        //do nothing
    }

    companion object {
        const val NAME: String = "Barge"
    }
}