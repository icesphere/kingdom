package com.kingdom.model.cards.allies

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.MultiTypePile
import com.kingdom.model.players.Player

abstract class AlliesCard(name: String, type: CardType, cost: Int, vararg additionalTypes: String) :
        Card(name, Deck.Allies, type, cost, additionalTypes = additionalTypes.toSet())

abstract class AlliesSplitCard(
        name: String,
        type: CardType,
        cost: Int,
        private val splitPileName: String,
        private val splitPileType: String,
        vararg additionalTypes: String
) : AlliesCard(name, type, cost, splitPileType, *additionalTypes) {
    override val pileName: String
        get() = splitPileName

    val isMultiTypePileMember: Boolean
        get() = true

    val multiTypePileName: String
        get() = splitPileName

    val multiTypePileCardNames: String
        get() = splitPileCardNamesByPile[splitPileName] ?: splitPileType

    companion object {
        private val splitPileCardNamesByPile = mapOf(
                "Augurs" to "Herb Gatherer, Acolyte, Sorceress, Sibyl",
                "Clashes" to "Battle Plan, Archer, Warlord, Territory",
                "Forts" to "Tent, Garrison, Hill Fort, Stronghold",
                "Odysseys" to "Old Map, Voyage, Sunken Treasure, Distant Shore",
                "Townsfolk" to "Town Crier, Blacksmith, Miller, Elder",
                "Wizards" to "Student, Conjurer, Sorcerer, Lich"
        )
    }
}

abstract class AlliesSplitPile(name: String, cost: Int) : AlliesCard(name, CardType.Action, cost), MultiTypePile {
    override val pileSize: Int = 16
}

internal fun Player.rotatePile(pileName: String) {
    game.rotatePile(pileName)
    addEventLogWithUsername("rotated the $pileName pile")
}

internal fun Player.gainTopCardFromPile(pileName: String, showLog: Boolean = true, destination: CardLocation = CardLocation.Discard) {
    val card = game.getTopCardFromPile(pileName) ?: return
    gainSupplyCard(card, showLog, destination)
}

internal fun Player.playCardFromDiscard(card: Card) {
    removeCardFromDiscard(card)
    addCardToHand(card)
    playCard(card)
}
