package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Fugitive : AdventuresCard(NAME, CardType.ActionTraveller, 4), CardDiscardedFromPlayListener, ChoiceActionCard {

    init {
        addCards = 2
        addActions = 1
        special = "Discard a card. When you discard this from play, you may exchange it for a Disciple. (This is not in the Supply.)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardFromHand()
    }

    override fun onCardDiscarded(player: Player) {
        val disciple = Disciple()

        if (player.game.isCardAvailableInSupply(disciple)) {
            player.yesNoChoice(this, "Exchange ${this.cardNameWithBackgroundColor} for a ${disciple.cardNameWithBackgroundColor}?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.exchangeDiscardedCard(this, Disciple())
        }
    }

    companion object {
        const val NAME: String = "Fugitive"
    }
}

