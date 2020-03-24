package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class TrashCardsFromHandForBenefit(private val trashCardsForBenefitActionCard: TrashCardsForBenefitActionCard, numCardsToScrap: Int, text: String, optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)? = null, private val info: Any? = null) : TrashCardsFromHand(numCardsToScrap, text, optional, cardActionableExpression) {

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val doneWithAction = super.processActionResult(player, result)

        if (doneWithAction) {
            trashCardsForBenefitActionCard.cardsTrashed(player, selectedCards, info)
        }

        return doneWithAction
    }

    override fun onNotUsed(player: Player) {
        if (trashCardsForBenefitActionCard is OptionalTrashCardsForBenefitActionCard) {
            trashCardsForBenefitActionCard.onCardsNotTrashed(player)
        }
    }
}