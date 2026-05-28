package com.kingdom.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class CardRepositoryTests {

    @Test
    fun seasideCardsUseSecondEditionList() {
        val seasideCardNames = CardRepository().seasideCards.map { it.name }

        assertEquals(listOf(
                "Astrolabe",
                "Bazaar",
                "Blockade",
                "Caravan",
                "Corsair",
                "Cutpurse",
                "Fishing Village",
                "Haven",
                "Island",
                "Lighthouse",
                "Lookout",
                "Merchant Ship",
                "Monkey",
                "Native Village",
                "Outpost",
                "Pirate",
                "Sailor",
                "Salvager",
                "Sea Chart",
                "Sea Witch",
                "Smugglers",
                "Tactician",
                "Tide Pools",
                "Treasure Map",
                "Treasury",
                "Warehouse",
                "Wharf"
        ), seasideCardNames)

        listOf(
                "Ambassador",
                "Embargo",
                "Explorer",
                "Ghost Ship",
                "Navigator",
                "Pearl Diver",
                "Pirate Ship",
                "Sea Hag"
        ).forEach { removedCardName ->
            assertFalse(seasideCardNames.contains(removedCardName))
        }
    }
}
