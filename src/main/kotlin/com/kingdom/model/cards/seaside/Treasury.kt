package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Treasury : SeasideCard(NAME, CardType.Action, 5), CardDiscardedFromPlayListener, ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "When you discard this from play, if you didn’t buy a Victory card this turn, you may put this onto your deck."
    }

    override fun onCardDiscarded(player: Player) {
        if (player.cardsBought.none { it.isVictory }) {
            player.yesNoChoice(this, "Put ${this.cardNameWithBackgroundColor} onto your deck?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.removeCardFromDiscard(this)
            player.addCardToTopOfDeck(this)
        }
    }

    companion object {
        const val NAME: String = "Treasury"
    }
}

