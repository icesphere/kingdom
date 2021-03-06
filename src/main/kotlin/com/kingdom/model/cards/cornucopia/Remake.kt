package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Remake : CornucopiaCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {

    var trashingSecondCard = false

    init {
        special = "Do this twice: Trash a card from your hand, then gain a card costing exactly \$1 more than it."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        isTrashingFromHandToUpgradeCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        trashCardFromHandForBenefit(player)
    }

    private fun trashCardFromHandForBenefit(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand, then gain a card costing exactly \$1 more than it.")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val card = trashedCards.first()

        if (player.game.availableCards.any { player.getCardCostWithModifiers(it) == player.getCardCostWithModifiers(card) + 1 }) {
            player.chooseSupplyCardToGainWithExactCost(player.getCardCostWithModifiers(card) + 1)
        } else {
            player.showInfoMessage("There were no cards available that cost \$${player.getCardCostWithModifiers(card) + 1}")
        }

        if (trashingSecondCard) {
            trashingSecondCard = false
        } else {
            if (player.hand.isNotEmpty()) {
                trashingSecondCard = true
                trashCardFromHandForBenefit(player)
            }
        }
    }

    companion object {
        const val NAME: String = "Remake"
    }
}

