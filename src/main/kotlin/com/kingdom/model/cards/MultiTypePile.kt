package com.kingdom.model.cards

import com.kingdom.model.Game

interface MultiTypePile {

    fun createMultiTypePile(game: Game): List<Card>

    val otherCardsInPile: List<Card>

}