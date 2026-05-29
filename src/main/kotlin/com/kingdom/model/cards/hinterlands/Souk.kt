package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Souk : HinterlandsCard(NAME, CardType.Action, 5), AfterCardGainedListenerForSelf, TrashCardsForBenefitActionCard {

    init {
        addBuys = 1
        special = "+\$7, minus \$1 per card in your hand. When you gain this, trash up to 2 cards from your hand."
        isTrashingCard = true
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCoins((7 - player.hand.size).coerceAtLeast(0))
    }

    override fun afterCardGained(player: Player) {
        player.optionallyTrashCardsFromHandForBenefit(this, 2, "Trash up to 2 cards from your hand")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        // No additional effect.
    }

    companion object {
        const val NAME: String = "Souk"
    }
}
