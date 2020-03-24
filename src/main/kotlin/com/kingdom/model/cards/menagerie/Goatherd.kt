package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.OptionalTrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Goatherd : MenagerieCard(NAME, CardType.Action, 3), OptionalTrashCardsForBenefitActionCard {

    init {
        addActions = 1
        special = "You may trash a card from your hand. +1 Card per card the player to your right trashed on their last turn"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHandForBenefit(this, 1, "You may trash a card from your hand")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        addCards(player)
    }

    private fun addCards(player: Player) {
        val previousPlayerCardsTrashedLastTurn = player.game.previousPlayer?.lastTurnSummary?.trashedCards

        if (previousPlayerCardsTrashedLastTurn?.isNotEmpty() == true) {
            player.drawCards(previousPlayerCardsTrashedLastTurn.size)
            player.addEventLog("Gained ${previousPlayerCardsTrashedLastTurn.size} cards from ${this.cardNameWithBackgroundColor}")
        }
    }

    override fun onCardsNotTrashed(player: Player) {
        addCards(player)
    }

    companion object {
        const val NAME: String = "Goatherd"
    }
}

