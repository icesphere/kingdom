package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class ChooseCardsAction(numCardsToChoose: Int,
                        text: String,
                        private val chooseCardsActionCard: ChooseCardsActionCard,
                        cardsToSelectFrom: List<Card>,
                        optional: Boolean,
                        private val info: Any?) : SelectCardsFromCardAction(text, cardsToSelectFrom, numCardsToChoose, optional) {

    override fun onSelectionDone(player: Player) {
        chooseCardsActionCard.onCardsChosen(player, selectedCards, info)
    }

}