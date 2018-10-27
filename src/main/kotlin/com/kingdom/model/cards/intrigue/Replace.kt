package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Replace : IntrigueCard(NAME, CardType.ActionAttack, 5), AttackCard, TrashCardsForBenefitActionCard, FreeCardFromSupplyForBenefitActionCard {

    init {
        special = "Trash a card from your hand. Gain a card costing up to \$2 more than it. If the gained card is an Action or Treasure, put it onto your deck; if it’s a Victory card, each other player gains a Curse."
        textSize = 115
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.trashCardsFromHandForBenefit(this, 1, special)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents.forEach { opponent ->
            opponent.acquireFreeCardFromSupply(Curse())
        }
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        val card = scrappedCards.first()
        player.acquireFreeCardForBenefit(player.getCardCostWithModifiers(card) + 2, "Gain a card costing up to \$${player.getCardCostWithModifiers(card) + 2}. If the gained card is an Action or Treasure, put it onto your deck; if it’s a Victory card, each other player gains a Curse.", this)
    }

    override fun isCardApplicable(card: Card): Boolean = true

    override fun onCardAcquired(player: Player, card: Card) {
        if (card.isAction || card.isTreasure) {
            //todo ideally the acquire action puts in on the deck
            if (player.cardsInDiscard.contains(card)) {
                player.removeCardFromDiscard(card)
                player.addCardToTopOfDeck(card)
            }
        }

        if (card.isVictory) {
            player.triggerAttack(this)
        }
    }

    companion object {
        const val NAME: String = "Replace"
    }
}

