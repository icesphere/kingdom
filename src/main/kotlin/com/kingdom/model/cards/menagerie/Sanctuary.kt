package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.players.Player

class Sanctuary : MenagerieCard(NAME, CardType.Action, 5), ChooseCardActionCardOptional, UsesExileMat {

    init {
        addCards = 1
        addActions = 1
        addBuys = 1
        special = "You may Exile a card from your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHandOptional(special, this)
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        card?.let { player.exileCardFromHand(it) }
    }

    companion object {
        const val NAME: String = "Sanctuary"
    }
}

