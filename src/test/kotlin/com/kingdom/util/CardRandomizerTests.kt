package com.kingdom.util

import com.kingdom.model.Game
import com.kingdom.model.RandomizingOptions
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.adventures.events.Alms
import com.kingdom.model.cards.allies.Bauble
import com.kingdom.model.cards.allies.Wizards
import com.kingdom.model.cards.menagerie.ways.WayOfTheSquirrel
import com.kingdom.model.cards.risingsun.Kitsune
import com.kingdom.model.cards.risingsun.Progress
import com.kingdom.repository.CardRepository
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
            isIncludeTraits = false
        }

        CardRandomizer(CardRepository()).setRandomKingdomCardsAndEvents(game, options)

        assertTrue(game.events.isEmpty())
        assertTrue(game.landmarks.isEmpty())
        assertTrue(game.projects.isEmpty())
        assertTrue(game.ways.isEmpty())
        assertTrue(game.traits.isEmpty())
    }

    @Test
    fun selectsAllyWhenKingdomHasLiaison() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        val repository = CardRepository()

        val options = RandomizingOptions().apply {
            customCardSelection = repository.baseCards.take(9) + Bauble()
            numEventsAndLandmarksAndProjectsAndWays = 0
        }

        CardRandomizer(repository).setRandomKingdomCardsAndEvents(game, options)

        assertNotNull(game.ally)
    }

    @Test
    fun selectsAllyWhenSplitPileHasLiaison() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        val repository = CardRepository()

        val options = RandomizingOptions().apply {
            customCardSelection = repository.baseCards.take(9) + Wizards()
            numEventsAndLandmarksAndProjectsAndWays = 0
        }

        CardRandomizer(repository).setRandomKingdomCardsAndEvents(game, options)

        assertNotNull(game.ally)
    }

    @Test
    fun clearsAllyWhenKingdomHasNoLiaison() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java))).apply {
            ally = CardRepository().allAllies.first()
        }
        val repository = CardRepository()

        val options = RandomizingOptions().apply {
            customCardSelection = repository.baseCards.take(10)
            numEventsAndLandmarksAndProjectsAndWays = 0
        }

        CardRandomizer(repository).setRandomKingdomCardsAndEvents(game, options)

        assertNull(game.ally)
    }

    @Test
    fun selectsProphecyWhenKingdomHasOmen() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        val repository = CardRepository()

        val options = RandomizingOptions().apply {
            customCardSelection = repository.baseCards.take(9) + Kitsune()
            numEventsAndLandmarksAndProjectsAndWays = 0
        }

        CardRandomizer(repository).setRandomKingdomCardsAndEvents(game, options)

        assertNotNull(game.prophecy)
    }

    @Test
    fun clearsProphecyWhenKingdomHasNoOmen() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java))).apply {
            prophecy = CardRepository().allProphecies.first()
        }
        val repository = CardRepository()

        val options = RandomizingOptions().apply {
            customCardSelection = repository.baseCards.take(10)
            numEventsAndLandmarksAndProjectsAndWays = 0
        }

        CardRandomizer(repository).setRandomKingdomCardsAndEvents(game, options)

        assertNull(game.prophecy)
    }

    @Test
    fun swapsProphecyForAnotherProphecy() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java))).apply {
            prophecy = Progress()
        }

        CardRandomizer(CardRepository()).swapProphecy(game, Progress.NAME)

        assertNotNull(game.prophecy)
        assertNotEquals(Progress.NAME, game.prophecy!!.name)
    }

    @Test
    fun swapsWayForAnotherWay() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        val wayToReplace = WayOfTheSquirrel()
        game.ways = mutableListOf(wayToReplace)
        game.randomizingOptions = RandomizingOptions().apply {
            isIncludeEvents = false
            isIncludeLandmarks = false
            isIncludeProjects = false
            isIncludeWays = true
            isIncludeTraits = false
        }

        CardRandomizer(CardRepository()).swapWay(game, wayToReplace.name)

        assertEquals(1, game.ways.size)
        assertNotEquals(wayToReplace.name, game.ways.first().name)
    }

    @Test
    fun swapsEventForWayWhenOnlyWaysAreAvailable() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        val eventToReplace = Alms()
        game.events = mutableListOf(eventToReplace)
        game.randomizingOptions = RandomizingOptions().apply {
            isIncludeEvents = false
            isIncludeLandmarks = false
            isIncludeProjects = false
            isIncludeWays = true
            isIncludeTraits = false
        }

        CardRandomizer(CardRepository()).swapEvent(game, eventToReplace.name)

        assertTrue(game.events.isEmpty())
        assertEquals(1, game.ways.size)
    }
}
