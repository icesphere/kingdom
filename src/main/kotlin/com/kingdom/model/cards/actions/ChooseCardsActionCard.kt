package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface ChooseCardsActionCard {

    fun onCardsChosen(player: Player, cards: List<Card>, info: Any? = null)
}