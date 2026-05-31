package com.kingdom.model

import com.kingdom.model.cards.plunder.Loot
import com.kingdom.model.cards.plunder.SackOfLoot
import com.kingdom.model.cards.plunder.Abundance
import com.kingdom.model.cards.plunder.traits.Cheap
import com.kingdom.model.cards.plunder.traits.Cursed
import com.kingdom.model.cards.risingsun.Kitsune
import com.kingdom.model.cards.risingsun.Progress
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class GameSetupTests {

    @Test
    fun setupAddsLootPileWhenKingdomUsesLoot() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        game.numPlayers = 2
        game.kingdomCards = mutableListOf(SackOfLoot())

        game.setupGame()

        assertTrue(game.isIncludeLoot)
        assertEquals(30, game.lootPile.size)
        assertEquals(30, game.numInPileMap[Loot.NAME])

        val gainedLoot = game.takeLoot()

        assertTrue(gainedLoot!!.additionalTypes.contains("Loot"))
        assertEquals(29, game.lootPile.size)
        assertEquals(29, game.numInPileMap[Loot.NAME])
    }

    @Test
    fun setupAssignsTraitsToEligibleKingdomPiles() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        game.numPlayers = 2
        game.kingdomCards = mutableListOf(Abundance())
        game.traits = mutableListOf(Cheap())

        game.setupGame()

        assertEquals(Abundance.NAME, game.traits.first().traitPileName)
    }

    @Test
    fun setupAddsLootPileWhenTraitUsesLoot() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        game.numPlayers = 2
        game.kingdomCards = mutableListOf(Abundance())
        game.traits = mutableListOf(Cursed())

        game.setupGame()

        assertTrue(game.isIncludeLoot)
        assertEquals(30, game.lootPile.size)
    }

    @Test
    fun setupAddsSunTokensWhenOmenAndProphecyArePresent() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        game.numPlayers = 2
        game.kingdomCards = mutableListOf(Kitsune())
        game.prophecy = Progress()

        game.setupGame()

        assertEquals(5, game.sunTokens)
    }
}
