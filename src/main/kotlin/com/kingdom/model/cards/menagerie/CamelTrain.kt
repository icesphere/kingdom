package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class CamelTrain : MenagerieCard(NAME, CardType.Action, 3), ChooseCardActionCard, UsesExileMat, AfterCardGainedListenerForSelf {

    init {
        special = "Exile a non-Victory card from the Supply. When you gain this, Exile a Gold from the Supply."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply(special, this, { !it.isVictory })
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.exileCardFromHand(card)
    }

    override fun afterCardGained(player: Player) {
        player.exileCardFromSupply(Gold())
    }

    companion object {
        const val NAME: String = "Camel Train"
    }
}

