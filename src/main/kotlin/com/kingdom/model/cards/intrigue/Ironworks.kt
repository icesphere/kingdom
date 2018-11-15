package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.players.Player

class Ironworks : IntrigueCard(NAME, CardType.Action, 4), FreeCardFromSupplyForBenefitActionCard {

    init {
        special = "Gain a card costing up to \$4. If the gained card is an… Action card, +1 Action; Treasure card, +\$1; Victory card, +1 Card"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainForBenefit(4, special, this)
    }

    override fun onCardGained(player: Player, card: Card) {
        if (card.isAction) {
            player.addUsernameGameLog("gained +1 Action from ${this.cardNameWithBackgroundColor}")
            player.addActions(1)
        }
        if (card.isTreasure) {
            player.addUsernameGameLog("gained +\$1 from ${this.cardNameWithBackgroundColor}")
            player.addCoins(1)
        }
        if (card.isVictory) {
            player.addUsernameGameLog("gained +1 Card from ${this.cardNameWithBackgroundColor}")
            player.drawCard()
        }
    }

    companion object {
        const val NAME: String = "Ironworks"
    }
}

