package com.kingdom.model.cards.menagerie

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class Barge : MenagerieCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction, ChoiceActionCard {

    private var used: Boolean = false

    init {
        special = "Either now or at the start of your next turn, +3 Cards and +1 Buy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, "+3 Cards and +1 Buy", Choice(1, "Now"), Choice(2, "Next Turn"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.drawCards(3)
            player.addBuys(1)
            used = true
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        if (!used) {
            player.drawCards(3)
            player.addBuys(1)
        }
        used = false
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        used = false
    }

    companion object {
        const val NAME: String = "Barge"
    }
}