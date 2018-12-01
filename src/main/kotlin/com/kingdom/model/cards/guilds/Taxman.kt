package com.kingdom.model.cards.guilds

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Taxman : GuildsCard(NAME, CardType.ActionAttack, 4), TrashCardsForBenefitActionCard, AttackCard {

    private var treasureCard: Card? = null

    init {
        special = "You may trash a Treasure from your hand. Each other player with 5 or more cards in hand discards a copy of it (or reveals they can't). Gain a Treasure onto your deck costing up to \$3 more than it."
        textSize = 121
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHandForBenefit(this, 1, special, { c -> c.isTreasure })
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        if (trashedCards.isNotEmpty()) {
            treasureCard = trashedCards.first()
            player.triggerAttack(this)
            player.chooseSupplyCardToGainToHandWithMaxCostAndType(player.getCardCostWithModifiers(trashedCards[0]) + 3, CardType.Treasure)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            if (opponent.hand.size >= 5) {
                if (opponent.hand.any { it.name == treasureCard!!.name }) {
                    opponent.discardCardFromHand(opponent.hand.first { it.name == treasureCard!!.name }, true)
                } else {
                    opponent.revealHand()
                }
            }
        }
    }

    companion object {
        const val NAME: String = "Taxman"
    }
}

