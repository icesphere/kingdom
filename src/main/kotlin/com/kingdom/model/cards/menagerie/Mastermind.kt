package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.MultipleTurnDuration
import com.kingdom.model.cards.NextTurnRepeater
import com.kingdom.model.cards.actions.CardRepeater
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.beforeCardRepeaterRepeated
import com.kingdom.model.cards.actions.clearRepeatedCards
import com.kingdom.model.cards.actions.handleCardToRepeatChosen
import com.kingdom.model.cards.actions.hasRepeatedDurationForCleanup
import com.kingdom.model.cards.actions.hasRepeatedMultipleTurnDurationToKeep
import com.kingdom.model.cards.listeners.TurnEndedListenerForDurationCards
import com.kingdom.model.players.Player

class Mastermind : MenagerieCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction, ChooseCardActionCardOptional, CardRepeater, NextTurnRepeater, TurnEndedListenerForDurationCards {

    override var cardBeingRepeated: Card? = null

    override val cardsBeingRepeated: MutableList<Card> = mutableListOf()

    override val timesRepeated: Int = 2

    private var turnsSincePlayed = 0

    init {
        special = "At the start of your next turn, you may play an Action card from your hand three times."
    }

    override fun durationStartOfTurnAction(player: Player) {
        if (turnsSincePlayed == 1) {
            player.chooseCardFromHandOptional("Choose an Action card from your hand to play three times", this) { c -> c.isAction }
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        handleCardToRepeatChosen(card, player)
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        clearRepeatedCards()
        turnsSincePlayed = 0
    }

    override fun beforeCardRepeated(player: Player) {
        beforeCardRepeaterRepeated()
    }

    override fun onTurnEnded(player: Player) {
        turnsSincePlayed++
    }

    override fun keepAtEndOfTurn(player: Player): Boolean {
        return (turnsSincePlayed == 1 && hasRepeatedDurationForCleanup()) || hasRepeatedMultipleTurnDurationToKeep(player)
    }

    companion object {
        const val NAME: String = "Mastermind"
    }
}
