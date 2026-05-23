package com.kingdom.mobile.network

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class SerializationTest {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    @Test
    fun commandRoundTrip() {
        val command = GameCommandRequest(
            type = "clickCard",
            location = "Hand",
            cardId = "card-1",
            cardName = "Copper"
        )

        val encoded = json.encodeToString(command)
        val decoded = json.decodeFromString<GameCommandRequest>(encoded)

        assertEquals(command, decoded)
    }

    @Test
    fun cardDefaultsDecode() {
        val decoded = json.decodeFromString<CardDto>(
            """
            {
              "id": "1",
              "name": "Copper",
              "pileName": "Copper",
              "deck": "Base",
              "type": "Treasure",
              "cost": 0,
              "adjustedCost": 0,
              "debtCost": 0,
              "text": "Treasure",
              "highlighted": false,
              "selected": false
            }
            """.trimIndent()
        )

        assertEquals("Copper", decoded.name)
        assertEquals(false, decoded.tradeRouteToken)
    }
}
