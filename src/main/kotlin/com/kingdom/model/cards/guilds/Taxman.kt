package com.kingdom.model.cards.guilds

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Taxman : GuildsCard(NAME, CardType.ActionAttack, 4), TrashCardsForBenefitActionCard, AttackCard {

    private var treasureCard: Card? = null

    init {
        special = "You may trash a Treasure from your hand. Each other player with 5 or more cards in hand discards a copy of it (or reveals they can't). Gain a Treasure onto your deck costing up to \$3 more than it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        player.optionallyTrashCardsFromHandForBenefit(this, 1, special, { c -> c.isTreasure }, affectedOpponents)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isNotEmpty()) {
            treasureCard = trashedCards.first()

            @Suppress("UNCHECKED_CAST")
            val affectedOpponents = info as List<Player>

            for (opponent in affectedOpponents) {
                if (opponent.hand.size >= 5) {
                    val treasureCard = opponent.hand.firstOrNull { it.name == treasureCard!!.name }
                    if (treasureCard != null) {
                        opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor discarded ${treasureCard.cardNameWithBackgroundColor} from your hand")
                        opponent.discardCardFromHand(treasureCard, true)
                    } else {
                        opponent.revealHand()
                    }
                }
            }

            player.chooseSupplyCardToGainWithMaxCost(player.getCardCostWithModifiers(trashedCards[0]) + 3, { c -> c.isTreasure }, "Gain a Treasure onto your deck costing up to ${player.getCardCostWithModifiers(trashedCards[0]) + 3}", CardLocation.Deck)
        }
    }

    companion object {
        const val NAME: String = "Taxman"
    }
}

