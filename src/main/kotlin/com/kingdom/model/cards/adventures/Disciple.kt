package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Disciple : AdventuresCard(NAME, CardType.ActionTraveller, 5), CardDiscardedFromPlayListener, ChoiceActionCard {

    init {
        special = "You main play an Action card from your hand twice. Gain a copy of it. When you discard this from play, you may exchange it for a Teacher. (This is not in the Supply.)"
        textSize = 100
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    override fun onCardDiscarded(player: Player) {
        val teacher = Teacher()

        if (player.game.isCardAvailableInSupply(teacher)) {
            player.yesNoChoice(this, "Exchange ${this.cardNameWithBackgroundColor} for a ${teacher.cardNameWithBackgroundColor}?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.exchangeDiscardedCard(this, Teacher())
        }
    }

    companion object {
        const val NAME: String = "Disciple"
    }
}

