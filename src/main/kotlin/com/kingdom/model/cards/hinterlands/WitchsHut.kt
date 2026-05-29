package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class WitchsHut : HinterlandsCard(NAME, CardType.ActionAttack, 5), AttackCard, DiscardCardsForBenefitActionCard {

    init {
        addCards = 4
        special = "Discard 2 cards, revealed. If they are both Actions, each other player gains a Curse."
        isCurseGiver = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsForBenefit(this, 2, "Discard 2 cards, revealed")
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        if (discardedCards.size == 2 && discardedCards.all { it.isAction }) {
            player.addEventLogWithUsername("revealed ${discardedCards.joinToString(", ") { it.cardNameWithBackgroundColor }}")
            player.triggerAttack(this)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            opponent.gainSupplyCard(Curse(), true)
        }
    }

    companion object {
        const val NAME: String = "Witch's Hut"
    }
}
