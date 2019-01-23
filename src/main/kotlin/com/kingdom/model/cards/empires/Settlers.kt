package com.kingdom.model.cards.empires

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.MultiTypePile
import com.kingdom.model.players.Player

class Settlers : EmpiresCard(NAME, CardType.Action, 2), MultiTypePile {

    init {
        addCards = 1
        addActions = 1
        special = "Look through your discard pile. You may reveal a Copper from it and put it into your hand. (Settlers is the top half of the Bustling Village  pile.)"
        textSize = 80
        disabled = true
    }

    override val otherCardsInPile: List<Card>
        get() = listOf(BustlingVillage())

    override fun createMultiTypePile(game: Game): List<Card> {
        return listOf(
                Settlers(),
                Settlers(),
                Settlers(),
                Settlers(),
                Settlers(),
                BustlingVillage(),
                BustlingVillage(),
                BustlingVillage(),
                BustlingVillage(),
                BustlingVillage()
        )
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Settlers"
    }
}

