package com.kingdom.model.cards.modifiers

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card

interface CardCostModifier {

    fun getChangeToCardCost(card: Card, player: Player): Int

}