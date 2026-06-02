package com.kingdom.model.cards.hinterlands

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.base.Village
import com.kingdom.model.players.HumanPlayer
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class BerserkerTests {

    @Test
    fun gainedBerserkerIsPlayedWhenPlayerHasActionInPlay() {
        val game = newGame()
        val player = HumanPlayer(User().apply { username = "Alice" }, game)
        game.players.add(player)
        val berserker = Berserker()
        player.inPlay.add(Village())

        player.cardGained(berserker)

        assertTrue(player.inPlay.contains(berserker))
        assertTrue(player.cardsPlayed.contains(berserker))
        assertFalse(player.cardsInDiscard.contains(berserker))
        assertFalse(player.hand.contains(berserker))
    }

    private fun newGame(): Game {
        return Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
    }
}
