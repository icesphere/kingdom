package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.CardRepeater
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.handleCardToRepeatChosen
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Disciple : AdventuresCard(NAME, CardType.ActionTraveller, 5), CardDiscardedFromPlayListener, ChoiceActionCard, ChooseCardActionCardOptional, CardRepeater {

    override var cardBeingRepeated: Card? = null

    override val timesRepeated: Int = 1

    init {
        special = "You may play an Action card from your hand twice. Gain a copy of it. When you discard this from play, you may exchange it for a Teacher. (This is not in the Supply.)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHandOptional("Choose an Action card from your hand to play twice", this, { c -> c.isAction })
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        handleCardToRepeatChosen(card, player)
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

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        cardBeingRepeated = null
    }

    companion object {
        const val NAME: String = "Disciple"
    }
}

