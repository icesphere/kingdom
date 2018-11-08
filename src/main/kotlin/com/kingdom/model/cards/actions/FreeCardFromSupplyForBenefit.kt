package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class FreeCardFromSupplyForBenefit(private val freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard, maxCost: Int?, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null, destination: CardLocation = CardLocation.Discard) : FreeCardFromSupply(maxCost, text, cardActionableExpression, destination) {

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val doneWithAction = super.processActionResult(player, result)

        if (doneWithAction) {
            freeCardFromSupplyForBenefitActionCard.onCardAcquired(player, result.selectedCard!!)
        }

        return doneWithAction
    }
}