package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer

class DiscardCardsFromHandForBenefit : DiscardCardsFromHand {
    private var discardCardsForBenefitActionCard: DiscardCardsForBenefitActionCard? = null

    constructor(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String) : super(numCardsToDiscard, text) {
        this.discardCardsForBenefitActionCard = card
    }

    constructor(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, optional: Boolean) : super(numCardsToDiscard, text, optional) {
        this.discardCardsForBenefitActionCard = card
    }

    override fun processActionResult(player: OldPlayer, result: ActionResult): Boolean {
        val doneWithAction = super.processActionResult(player, result)

        if (doneWithAction) {
            discardCardsForBenefitActionCard!!.cardsDiscarded(player, selectedCards)
        }

        return doneWithAction
    }

    override fun onNotUsed(player: OldPlayer) {
        discardCardsForBenefitActionCard!!.onChoseDoNotUse(player)
    }
}