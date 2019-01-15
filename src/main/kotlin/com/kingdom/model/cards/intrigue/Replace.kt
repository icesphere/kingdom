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
        isTrashingFromHandRequiredCard = true
        isTrashingFromHandToUpgradeCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.trashCardsFromHandForBenefit(this, 1, special)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents.forEach { opponent ->
            val curse = Curse()
            if (opponent.game.isCardAvailableInSupply(curse)) {
                opponent.gainSupplyCard(curse, showLog = true)
                opponent.showInfoMessage("You gained a ${curse.cardNameWithBackgroundColor} when ${player.username} gained a Victory card with $cardNameWithBackgroundColor")
            }
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        val card = trashedCards.first()
        player.chooseSupplyCardToGainForBenefit(player.getCardCostWithModifiers(card) + 2, "Gain a card costing up to \$${player.getCardCostWithModifiers(card) + 2}. If the gained card is an Action or Treasure, put it onto your deck; if it’s a Victory card, each other player gains a Curse.", this)
    }

    override fun onCardGained(player: Player, card: Card) {
        if (card.isAction || card.isTreasure) {
            //todo ideally the gain action puts it on the deck
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

