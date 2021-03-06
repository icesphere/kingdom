package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class FreeCardFromSupplyForBenefit(private val freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null, destination: CardLocation = CardLocation.Discard) : FreeCardFromSupply(text, cardActionableExpression, destination) {

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val doneWithAction = super.processActionResult(player, result)

        if (doneWithAction) {
            freeCardFromSupplyForBenefitActionCard.onCardGained(player, result.selectedCard!!)
        }

        return doneWithAction
    }
}