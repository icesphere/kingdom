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

    @Test
    fun prosperityCardsUseSecondEditionList() {
        val prosperityCardNames = CardRepository().prosperityCards.map { it.name }

        assertEquals(listOf(
                "Anvil",
                "Bank",
                "Bishop",
                "Charlatan",
                "City",
                "Clerk",
                "Collection",
                "Crystal Ball",
                "Expand",
                "Forge",
                "Grand Market",
                "Hoard",
                "Investment",
                "King's Court",
                "Magnate",
                "Mint",
                "Monument",
                "Peddler",
                "Quarry",
                "Rabble",
                "Tiara",
                "Vault",
                "War Chest",
                "Watchtower",
                "Worker's Village"
        ), prosperityCardNames)

        listOf(
                "Contraband",
                "Counting House",
                "Goons",
                "Loan",
                "Mountebank",
                "Royal Seal",
                "Talisman",
                "Trade Route",
                "Venture"
        ).forEach { removedCardName ->
            assertFalse(prosperityCardNames.contains(removedCardName))
        }
    }
}
