package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class ChooseCardForBenefit(text: String,
                           private val chooseCardForBenefitActionCard: ChooseCardForBenefitActionCard,
                           cardsToSelectFrom: List<Card>,
                           optional: Boolean) : SelectCardsAction(text, cardsToSelectFrom, 1, optional) {

    override val isShowDone: Boolean = false

    init {
        this.isShowDoNotUse = optional
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        chooseCardForBenefitActionCard.onCardChosen(player, result.selectedCard!!)
        return true
    }

}