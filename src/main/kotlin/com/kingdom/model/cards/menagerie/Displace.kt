package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Displace : MenagerieCard(NAME, CardType.Action, 5), ChooseCardActionCard, UsesExileMat {

    init {
        special = "Exile a card from your hand. Gain a differently named card costing up to \$2 more than it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand(special, this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.exileCardFromHand(card)
        player.chooseSupplyCardToGainWithMaxCost(player.getCardCostWithModifiers(card) + 2, { it.name != card.name })
    }

    companion object {
        const val NAME: String = "Displace"
    }
}

