package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Hero : AdventuresCard(NAME, CardType.ActionTraveller, 5), CardDiscardedFromPlayListener, ChoiceActionCard {

    init {
        addCoins = 2
        special = "Gain a Treasure. When you discard this from play, you may exchange it for a Champion. (This is not in the Supply.)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGain({ c -> c.isTreasure }, "Gain a Treasure from the Supply")
    }

    override fun onCardDiscarded(player: Player) {
        val champion = Champion()

        if (player.game.isCardAvailableInSupply(champion)) {
            player.yesNoChoice(this, "Exchange ${this.cardNameWithBackgroundColor} for a ${champion.cardNameWithBackgroundColor}?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.exchangeDiscardedCard(this, Champion())
        }
    }

    companion object {
        const val NAME: String = "Hero"
    }
}

