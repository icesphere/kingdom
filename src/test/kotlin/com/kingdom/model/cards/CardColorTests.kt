package com.kingdom.model.cards

import com.kingdom.model.cards.allies.Stronghold
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CardColorTests {

    @Test
    fun actionDurationVictoryCardsUseDurationVictorySplitColor() {
        assertEquals(CardColor.DurationVictory, Stronghold().backgroundColor)
    }
}
