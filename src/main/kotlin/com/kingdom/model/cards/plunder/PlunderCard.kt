package com.kingdom.model.cards.plunder

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class PlunderCard(name: String, type: CardType, cost: Int, vararg additionalTypes: String) :
        Card(name, Deck.Plunder, type, cost, additionalTypes = additionalTypes.toSet())

abstract class LootCard(name: String, type: CardType = CardType.Treasure) :
        Card(name, Deck.None, type, 7, additionalTypes = setOf("Loot"))

interface UsesLoot

class Loot : Card(NAME, Deck.None, CardType.Treasure, 7,
        special = "A shuffled non-supply pile of Loot cards.",
        additionalTypes = setOf("Loot")) {

    companion object {
        const val NAME = "Loot"
    }
}

fun allLootCards(): List<Card> = listOf(
        Amphora(),
        Doubloons(),
        EndlessChalice(),
        Figurehead(),
        Hammer(),
        Insignia(),
        Jewels(),
        Orb(),
        PrizeGoat(),
        PuzzleBox(),
        Sextant(),
        Shield(),
        SpellScroll(),
        Staff(),
        Sword()
)
