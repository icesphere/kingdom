package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.MultipleTurnDuration
import com.kingdom.model.cards.NextTurnRepeater
import com.kingdom.model.cards.actions.CardRepeater
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.handleCardToRepeatChosen
import com.kingdom.model.players.Player

class Mastermind : MenagerieCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction, ChooseCardActionCardOptional, CardRepeater, NextTurnRepeater {

    override var cardBeingRepeated: Card? = null

    override val timesRepeated: Int = 2

    override var isNextTurn: Boolean = false

    init {
        special = "At the start of your next turn, you may play an Action card from your hand three times."
        testing = true
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.chooseCardFromHandOptional("Choose an Action card from your hand to play three times", this) { c -> c.isAction }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        handleCardToRepeatChosen(card, player)
    }

    override fun removedFromPlay(player: Player) {
        cardBeingRepeated = null
    }

    override fun keepAtEndOfTurn(player: Player): Boolean {
        return (!isNextTurn && cardBeingRepeated?.isDuration == true) || (cardBeingRepeated is MultipleTurnDuration && (cardBeingRepeated as MultipleTurnDuration).keepAtEndOfTurn(player))
    }

    companion object {
        const val NAME: String = "Mastermind"
    }
}