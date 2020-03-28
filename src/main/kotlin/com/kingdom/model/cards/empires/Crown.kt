package com.kingdom.model.cards.empires

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.CardRepeater
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.handleCardToRepeatChosen
import com.kingdom.model.players.Player

class Crown : EmpiresCard(NAME, CardType.ActionTreasure, 5), ChooseCardActionCardOptional, CardRepeater {

    override var cardBeingRepeated: Card? = null

    override val timesRepeated: Int = 1

    init {
        special = "If it’s your Action phase, you may play an Action from your hand twice. If it’s your Buy phase, you may play a Treasure from your hand twice."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val text = if (!player.isBuyPhase) {
            "Choose an Action or Treasure card from your hand to play twice"
        } else {
            "Choose a Treasure card from your hand to play twice"
        }
        player.chooseCardFromHandOptional(text, this) { c -> c.isTreasure || (!player.isBuyPhase && c.isAction) }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        handleCardToRepeatChosen(card, player)
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        cardBeingRepeated = null
    }

    companion object {
        const val NAME: String = "Crown"
    }
}

