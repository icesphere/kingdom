package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player

class DiscardCardsFromHandForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, optional: Boolean) : DiscardCardsFromHand(numCardsToDiscard, text, optional) {
    private var discardCardsForBenefitActionCard: DiscardCardsForBenefitActionCard? = card

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val doneWithAction = super.processActionResult(player, result)

        if (doneWithAction) {
            discardCardsForBenefitActionCard!!.cardsDiscarded(player, selectedCards)
        }

        return doneWithAction
    }

    override fun onNotUsed(player: Player) {
        discardCardsForBenefitActionCard!!.onChoseDoNotUse(player)
    }
}