package com.kingdom.model.cards.actions

import com.kingdom.model.Player

class TrashCardsFromHandForBenefit(private val trashCardsForBenefitActionCard: TrashCardsForBenefitActionCard, numCardsToScrap: Int, text: String) : TrashCardsFromHand(numCardsToScrap, text) {

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val doneWithAction = super.processActionResult(player, result)

        if (doneWithAction) {
            trashCardsForBenefitActionCard.cardsScrapped(player, selectedCards)
        }

        return doneWithAction
    }
}