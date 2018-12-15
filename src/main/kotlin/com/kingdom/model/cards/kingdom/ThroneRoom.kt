package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.players.Player

class ThroneRoom : KingdomCard(NAME, CardType.Action, 4), ChooseCardActionCardOptional, CardRepeater {

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
        cardBeingRepeated = null
    }

    companion object {
        const val NAME: String = "Throne Room"
    }
}

