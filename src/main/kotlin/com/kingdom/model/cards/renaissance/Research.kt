package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.SetAsideCardsDuration
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Research : RenaissanceCard(NAME, CardType.ActionDuration, 4), StartOfTurnDurationAction, TrashCardsForBenefitActionCard, SetAsideCardsDuration {

    override var setAsideCards = mutableListOf<Card>()

    init {
        addActions = 1
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        special = "Trash a card from your hand. Per \$1 it costs, set aside a card from your deck face down (on this). At the start of your next turn, put those cards into your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand. Per \$1 it costs, set aside a card from your deck face down (on this).")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val cost = player.getCardCostWithModifiers(trashedCards.first())
        if (cost > 0) {
            setAsideCards = player.removeTopCardsOfDeck(cost).toMutableList()
            player.showInfoMessage("Set aside ${setAsideCards.groupedString} with $cardNameWithBackgroundColor")
        } else {
            setAsideCards.clear()
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        if (setAsideCards.isNotEmpty()) {
            player.addCardsToHand(setAsideCards)
            player.showInfoMessage("$cardNameWithBackgroundColor added ${setAsideCards.groupedString} to your hand")
            setAsideCards.clear()
        }
    }

    companion object {
        const val NAME: String = "Research"
    }
}