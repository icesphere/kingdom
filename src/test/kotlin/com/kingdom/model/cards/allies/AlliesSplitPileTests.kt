package com.kingdom.model.cards.allies

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AlliesSplitPileTests {

    @Test
    fun splitPileCardsExposeTheirPileAndMemberNames() {
        val student = Student()

        assertTrue(student.isMultiTypePileMember)
        assertEquals(Wizards.NAME, student.multiTypePileName)
        assertEquals("Student, Conjurer, Sorcerer, Lich", student.multiTypePileCardNames)
    }
}
