package com.kingdom.model.players

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.supply.Copper
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class PlayerAutoEndTests {

    @Test
    fun `only buy remains when player has no playable cards`() {
        val player = currentPlayer()
        player.hand.clear()

        assertTrue(player.isOnlyBuyDecisionRemaining())
    }

    @Test
    fun `playable treasures prevent only-buy auto end`() {
        val player = currentPlayer()
        player.hand.clear()
        player.hand.add(Copper())

        assertFalse(player.isOnlyBuyDecisionRemaining())
    }

    private fun currentPlayer(): HumanPlayer {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        val player = HumanPlayer(User().apply { username = "Alice" }, game)
        game.players.add(player)
        player.startTurn(false)
        return player
    }
}
