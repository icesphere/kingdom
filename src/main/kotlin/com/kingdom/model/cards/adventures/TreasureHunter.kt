package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class TreasureHunter : AdventuresCard(NAME, CardType.ActionTraveller, 3), CardDiscardedFromPlayListener, ChoiceActionCard {

    init {
        addActions = 1
        addCoins = 1
        special = "Gain a Silver per card the player to your right gained on their last turn. When you discard this from play, you may exchange it for a Warrior. (This is not in the Supply.)"
        fontSize = 10
        textSize = 83
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val numCardsGainedLastTurn = player.game.previousPlayer?.lastTurnSummary?.cardsGained?.size ?: 0
        if (numCardsGainedLastTurn > 0) {
            repeat(numCardsGainedLastTurn) {
                player.gainSupplyCard(Silver())
            }
        }
    }

    override fun onCardDiscarded(player: Player) {
        val warrior = Warrior()

        if (player.game.isCardAvailableInSupply(warrior)) {
            player.yesNoChoice(this, "Exchange ${this.cardNameWithBackgroundColor} for a ${warrior.cardNameWithBackgroundColor}?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.exchangeDiscardedCard(this, Warrior())
        }
    }

    companion object {
        const val NAME: String = "Treasure Hunter"
    }
}

