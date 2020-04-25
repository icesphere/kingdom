package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.cards.menagerie.UsesHorses
import com.kingdom.model.players.Player

class Bargain : MenagerieEvent(NAME, 4), FreeCardFromSupplyForBenefitActionCard, UsesHorses {

    init {
        special = "Gain a non-Victory card costing up to \$5. Each other player gains a Horse."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.game.availableCards.any { player.getCardCostWithModifiers(it) <= 5 && !it.isVictory }
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainForBenefitWithMaxCost(5, special, this) { !it.isVictory }
    }

    override fun onCardGained(player: Player, card: Card) {
        player.opponentsInOrder.forEach {
            it.gainHorse()
        }
    }

    companion object {
        const val NAME: String = "Bargain"
    }
}