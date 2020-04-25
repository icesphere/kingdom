package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Groom : MenagerieCard(NAME, CardType.Action, 4), FreeCardFromSupplyForBenefitActionCard, UsesHorses {

    init {
        special = "Gain a card costing up to \$4. If it's an... Action card, gain a Horse; Treasure card, gain a Silver; Victory card, +1 Card and +1 Action."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainForBenefitWithMaxCost(4, special, this)
    }

    override fun onCardGained(player: Player, card: Card) {
        if (card.isAction) {
            player.gainHorse()
        }
        if (card.isTreasure) {
            player.gainSupplyCard(Silver(), true)
        }
        if (card.isVictory) {
            player.addEventLogWithUsername("gained +1 Card, +1 Action from ${this.cardNameWithBackgroundColor}")
            player.drawCard()
            player.addActions(1)
        }
    }

    companion object {
        const val NAME: String = "Groom"
    }
}

