package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class TrashCardsFromHandForBenefit(private val trashCardsForBenefitActionCard: TrashCardsForBenefitActionCard, numCardsToScrap: Int, text: String, optional: Boolean) : TrashCardsFromHand(numCardsToScrap, text) {

    init {
        isShowDoNotUse = optional
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return super.isCardActionable(card, cardLocation, player) && trashCardsForBenefitActionCard.isCardApplicable(card)
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val doneWithAction = super.processActionResult(player, result)

        if (doneWithAction) {
            trashCardsForBenefitActionCard.cardsScrapped(player, selectedCards)
        }

        return doneWithAction
    }
}