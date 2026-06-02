package com.kingdom.model.players

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.springframework.messaging.simp.SimpMessagingTemplate

class PlayerRefreshTests {

    @Test
    fun `adding buys refreshes supply buy highlights`() {
        val messagingTemplate = mock(SimpMessagingTemplate::class.java)
        val game = Game(GameManager(), GameMessageService(messagingTemplate))
        val player = HumanPlayer(User().apply { username = "Alice" }, game)
        game.players.add(player)
        player.startTurn(false)
        reset(messagingTemplate)

        player.addBuys(1)

        verify(messagingTemplate).convertAndSend("/queue/refresh-supply/${player.userId}", "refresh")
        verify(messagingTemplate).convertAndSend("/queue/refresh-cards-bought/${player.userId}", "refresh")
    }
}
