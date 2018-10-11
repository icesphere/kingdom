package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class TradingPost : IntrigueCard(NAME, CardType.Action, 5) {

    init {
        testing = true
        special = "Trash 2 cards from your hand. If you did, gain a Silver to your hand."
    }

    override val isTrashingCard: Boolean = true

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            when (player.hand.size) {
                1 -> player.trashCardFromHand(player.hand.first())
                2 -> {
                    player.hand.forEach { player.trashCardFromHand(it) }
                    player.acquireFreeCardFromSupplyToHand(Silver())
                }
                else -> {
                    player.trashCardsFromHand(2, false)
                    player.acquireFreeCardFromSupplyToHand(Silver())
                }
            }
        }
    }

    companion object {
        const val NAME: String = "Trading Post"
    }
}

