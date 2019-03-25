package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Sculptor : RenaissanceCard(NAME, CardType.Action, 5), FreeCardFromSupplyForBenefitActionCard {

    init {
        special = "Gain a card to your hand costing up to \$4. If it’s a Treasure, +1 Villager."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.isNextCardToHand = true
        player.chooseSupplyCardToGainForBenefitWithMaxCost(4, special, this)
    }

    override fun onCardGained(player: Player, card: Card) {
        if (card.isTreasure) {
            player.addVillagers(1)
            player.addEventLogWithUsername("gained +1 Villager from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Sculptor"
    }
}