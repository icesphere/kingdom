package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.CardGainedListenerForCardsInHand
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Trader : HinterlandsCard(NAME, CardType.ActionReaction, 4), TrashCardsForBenefitActionCard, CardGainedListenerForCardsInHand, ChoiceActionCard {

    init {
        special = "Trash a card from your hand. Gain a Silver per \$1 it costs. When you would gain a card, you may reveal this from your hand, to instead gain a Silver."
        textSize = 122
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        val card = scrappedCards.first()

        repeat(player.getCardCostWithModifiers(card)) {
            player.acquireFreeCardFromSupply(Silver(), true)
        }
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        if (card.isSilver) {
            return false
        }

        player.yesNoChoice(this, "Reveal ${this.cardNameWithBackgroundColor} to gain a ${Silver().cardNameWithBackgroundColor} instead of ${card.cardNameWithArticleAndBackgroundColor}?", card)
        return true
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = info as Card

        if (choice == 1) {
            player.cardAcquired(Silver())
            player.addUsernameGameLog("revealed ${this.cardNameWithBackgroundColor} to gain a ${Silver().cardNameWithBackgroundColor} instead of ${card.cardNameWithArticleAndBackgroundColor}")
        } else {
            player.cardAcquired(card)
        }
    }

    override fun isCardApplicable(card: Card): Boolean = true

    companion object {
        const val NAME: String = "Trader"
    }
}

