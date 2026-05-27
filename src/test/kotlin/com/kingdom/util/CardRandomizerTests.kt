package com.kingdom.util

import com.kingdom.model.Game
import com.kingdom.model.RandomizingOptions
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.adventures.events.Alms
import com.kingdom.repository.CardRepository
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class CardRandomizerTests {

    @Test
    fun excludesEventsLandmarksProjectsAndWaysWhenMarkedNone() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        game.decks = mutableListOf(Deck.Base)

        val options = RandomizingOptions().apply {
            numEventsAndLandmarksAndProjectsAndWays = 3
            customEventSelection = listOf(Alms())
            isIncludeEvents = false
            isIncludeLandmarks = false
            isIncludeProjects = false
            isIncludeWays = false
        }

        CardRandomizer(CardRepository()).setRandomKingdomCardsAndEvents(game, options)

        assertTrue(game.events.isEmpty())
        assertTrue(game.landmarks.isEmpty())
        assertTrue(game.projects.isEmpty())
        assertTrue(game.ways.isEmpty())
    }
}
