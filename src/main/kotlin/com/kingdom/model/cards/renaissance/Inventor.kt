package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Inventor : RenaissanceCard(NAME, CardType.Action, 4), FreeCardFromSupplyForBenefitActionCard, CardCostModifier {

    init {
        special = "Gain a card costing up to \$4, then cards cost \$1 less this turn (but not less than \$0)."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainForBenefitWithMaxCost(4, special, this)
    }

    override fun onCardGained(player: Player, card: Card) {
        player.game.currentPlayerCardCostModifiers.add(this)
        player.game.refreshSupply()
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        return -1
    }

    companion object {
        const val NAME: String = "Inventor"
    }
}