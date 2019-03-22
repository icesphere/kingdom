package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterCardTrashedListener
import com.kingdom.model.players.Player

class Priest : RenaissanceCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard, AfterCardTrashedListener {

    init {
        addCoins = 2
        special = "Trash a card from your hand. For the rest of this turn, when you trash a card, +\$2."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        player.currentTurnCardTrashedListeners.add(this)
    }

    override fun afterCardTrashed(card: Card, player: Player) {
        player.addCoins(2)
        player.addEventLogWithUsername("gained +\$2 from $cardNameWithBackgroundColor")
    }

    companion object {
        const val NAME: String = "Priest"
    }
}