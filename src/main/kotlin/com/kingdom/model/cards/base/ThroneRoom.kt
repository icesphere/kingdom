package com.kingdom.model.cards.base

import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.CardRepeater
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.handleCardToRepeatChosen
import com.kingdom.model.players.Player

class ThroneRoom : BaseCard(NAME, CardType.Action, 4), ChooseCardActionCardOptional, CardRepeater {

    override var cardBeingRepeated: Card? = null

    override val timesRepeated: Int = 1

    init {
        special = "You may play an Action card from your hand twice."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHandOptional("Choose an Action card from your hand to play twice", this, { c -> c.isAction })
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        handleCardToRepeatChosen(card, player)
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        cardBeingRepeated = null
    }

    companion object {
        const val NAME: String = "Throne Room"
    }
}

