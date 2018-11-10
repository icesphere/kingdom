package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class ChooseCardAction(text: String,
                       private val chooseCardActionCard: ChooseCardActionCard,
                       cardsToSelectFrom: List<Card>,
                       optional: Boolean,
                       private val info: Any?) : SelectCardsFromCardAction(text, cardsToSelectFrom, 1, optional) {

    override fun onSelectionDone(player: Player) {
        chooseCardActionCard.onCardChosen(player, selectedCards.first(), info)
    }

}