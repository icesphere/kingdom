package com.kingdom.model.cards.empires

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForCardsInPlay
import com.kingdom.model.players.Player

class Charm : EmpiresCard(NAME, CardType.Treasure, 5), ChoiceActionCard, AfterCardBoughtListenerForCardsInPlay {

    var isGainCardOnNextBuyThisTurn: Boolean = false

    init {
        special = "When you play this, choose one: +1 Buy and +\$2; or the next time you buy a card this turn, you may also gain a differently named card with the same cost."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, "Choose one: +1 Buy and +\$2; or the next time you buy a card this turn, you may also gain a differently named card with the same cost.", Choice(1, "+1 Buy and +\$2"), Choice(2, "Next buy gain card"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addBuys(1)
            player.addCoins(2)
            player.addEventLogWithUsername("chose +1 Buy and +\$2")
        } else {
            isGainCardOnNextBuyThisTurn = true
            player.addEventLogWithUsername("chose next buy this turn to gain a card")
        }
    }

    override fun afterCardBought(card: Card, player: Player) {
        if (isGainCardOnNextBuyThisTurn) {
            isGainCardOnNextBuyThisTurn = false
            player.chooseSupplyCardToGain(null, { c -> c.debtCost == card.debtCost && c.cost == player.getCardCostWithModifiers(card) && c.name != card.name })
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        isGainCardOnNextBuyThisTurn = false
    }

    companion object {
        const val NAME: String = "Charm"
    }
}

