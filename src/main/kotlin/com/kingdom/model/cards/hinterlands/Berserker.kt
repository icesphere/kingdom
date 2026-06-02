package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Berserker : HinterlandsCard(NAME, CardType.ActionAttack, 5), AttackCard, AfterCardGainedListenerForSelf,
        FreeCardFromSupplyForBenefitActionCard {

    init {
        special = "Gain a card costing less than this. Each other player discards down to 3 cards in hand. When you gain this, if you have an Action in play, play this."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val maxCost = player.getCardCostWithModifiers(this) - 1
        if (player.game.availableCards.any { it.debtCost == 0 && player.getCardCostWithModifiers(it) <= maxCost }) {
            player.chooseSupplyCardToGainForBenefitWithMaxCost(maxCost, "Gain a card costing less than ${cardNameWithBackgroundColor}", this)
        } else {
            triggerDiscardAttack(player)
        }
    }

    override fun onCardGained(player: Player, card: Card) {
        triggerDiscardAttack(player)
    }

    private fun triggerDiscardAttack(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            if (opponent.hand.size > 3) {
                opponent.discardCardsFromHand(opponent.hand.size - 3, false)
            }
        }
    }

    override fun afterCardGained(player: Player) {
        if (player.inPlayWithDuration.any { it.isAction }) {
            when {
                player.cardsInDiscard.contains(this) -> player.removeCardFromDiscard(this)
                player.deck.contains(this) -> player.removeCardFromDeck(this)
                player.hand.contains(this) -> Unit
                else -> return
            }
            if (!player.hand.contains(this)) {
                player.addCardToHand(this)
            }
            if (player.isYourTurn) {
                player.addActions(1, false)
            }
            player.playCard(this)
        }
    }

    companion object {
        const val NAME: String = "Berserker"
    }
}
