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
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        isTrashingFromHandToUpgradeCard = true
    }

    private var affectedOpponents = emptyList<Player>()

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        if (player.hand.isNotEmpty()) {
            this.affectedOpponents = affectedOpponents

            player.trashCardsFromHandForBenefit(this, 1, special)
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
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
            affectedOpponents.forEach { opponent ->
                val curse = Curse()
                if (opponent.game.isCardAvailableInSupply(curse)) {
                    opponent.gainSupplyCard(curse, showLog = true)
                    opponent.showInfoMessage("You gained a ${curse.cardNameWithBackgroundColor} when ${player.username} gained a Victory card with $cardNameWithBackgroundColor")
                }
            }
        }
    }

    companion object {
        const val NAME: String = "Replace"
    }
}

