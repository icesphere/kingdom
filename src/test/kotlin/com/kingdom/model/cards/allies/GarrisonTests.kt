package com.kingdom.model.cards.allies

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.HumanPlayer
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class GarrisonTests {

    @Test
    fun keepsUntilNextTurnAndDrawsForGainedCardTokens() {
        val player = HumanPlayer(User().apply { username = "Alice" }, newGame())
        player.deck.add(Copper())
        val garrison = Garrison()

        garrison.afterCardGained(Silver(), player)

        assertTrue(garrison.isKeepAtEndOfTurn)

        val handSizeBeforeDuration = player.hand.size
        garrison.durationStartOfTurnAction(player)

        assertEquals(handSizeBeforeDuration + 1, player.hand.size)
        assertFalse(garrison.isKeepAtEndOfTurn)
    }

    private fun newGame(): Game {
        return Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
    }
}
