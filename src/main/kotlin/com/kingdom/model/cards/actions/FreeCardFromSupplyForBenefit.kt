package com.kingdom.model.cards.actions

import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class FreeCardFromSupplyForBenefit(private val freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard, maxCost: Int?, text: String, destination: CardLocation = CardLocation.Discard, cardType: CardType? = null) : FreeCardFromSupply(maxCost, text, destination, cardType) {

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val doneWithAction = super.processActionResult(player, result)

        if (doneWithAction) {
            freeCardFromSupplyForBenefitActionCard.onCardAcquired(player, result.selectedCard!!)
        }

        return doneWithAction
    }
}