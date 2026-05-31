package com.kingdom.model.cards

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserDeckInfoTests {

    @Test
    fun risingSunUsesDisplayNameWithSpace() {
        val deckInfo = UserDeckInfo(Deck.RisingSun, emptyList())

        assertEquals("Rising Sun", deckInfo.displayName)
    }
}
