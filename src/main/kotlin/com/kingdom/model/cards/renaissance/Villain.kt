package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Villain : RenaissanceCard(NAME, CardType.ActionAttack, 5), AttackCard, DiscardCardsForBenefitActionCard {

    init {
        addCoffers = 2
        special = "Each other player with 5 or more cards in hand discards one costing \$2 or more (or reveals they can’t)."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.filter { it.hand.size >= 5 }
                .forEach { opponent ->
                    if (opponent.hand.any { player.getCardCostWithModifiers(it) >= 2 }) {
                        opponent.discardCardsForBenefit(this, 1, "Discard a card costing \$2 or more") { c -> player.getCardCostWithModifiers(c) >= 2 }
                    } else {
                        opponent.revealHand()
                        player.showInfoMessage("${opponent.username} did not have any cards costing \$2 or more")
                    }
                }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        player.game.currentPlayer.showInfoMessage("${player.username} discarded ${discardedCards.first().cardNameWithBackgroundColor}")
    }

    companion object {
        const val NAME: String = "Villain"
    }
}