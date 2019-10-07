package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.CardRepeater
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.handleCardToRepeatChosen
import com.kingdom.model.players.Player

class KingsCourt : ProsperityCard(NAME, CardType.Action, 7), ChooseCardActionCardOptional, CardRepeater {

    override var cardBeingRepeated: Card? = null

    override val timesRepeated: Int = 2

    init {
        special = "You may play an Action card from your hand three times."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHandOptional("Choose an Action card from your hand to play three times", this) { c -> c.isAction }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        handleCardToRepeatChosen(card, player)
    }

    override fun removedFromPlay(player: Player) {
        cardBeingRepeated = null
    }

    companion object {
        const val NAME: String = "Kings Court"
    }
}

