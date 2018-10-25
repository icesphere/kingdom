package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Haven : SeasideCard(NAME, CardType.ActionDuration, 2), StartOfTurnDurationAction, ChooseCardActionCard {

    var setAsideCard: Card? = null

    init {
        addCards = 1
        addActions = 1
        special = "Set aside a card from your hand face down (under this). At the start of your next turn, put it into your hand."
        textSize = 81
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.chooseCardFromHand(special, this)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardFromHand(card)
        setAsideCard = card
    }

    override fun durationStartOfTurnAction(player: Player) {
        setAsideCard?.let {
            player.addCardToHand(it)
        }

        setAsideCard = null
    }

    companion object {
        const val NAME: String = "Haven"
    }
}
