package com.kingdom.model.cards.empires

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.players.HumanPlayer
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class GladiatorTests {

    @Test
    fun `player to left automatically reveals matching card`() {
        val game = newGame()
        val player = HumanPlayer(User().apply { username = "Alice" }, game)
        val playerToLeft = HumanPlayer(User().apply { username = "Bob" }, game)
        game.players.add(player)
        game.players.add(playerToLeft)
        player.hand.clear()
        playerToLeft.hand.clear()
        val revealedCard = Copper()
        val matchingCard = Copper()
        player.hand.add(revealedCard)
        playerToLeft.hand.add(matchingCard)

        Gladiator().onCardChosen(player, revealedCard)

        assertEquals(0, player.availableCoins)
        assertNull(playerToLeft.currentAction)
        assertEquals(listOf(matchingCard), playerToLeft.hand)
    }

    @Test
    fun `player gains bonus when player to left has no matching card`() {
        val game = newGame()
        val player = HumanPlayer(User().apply { username = "Alice" }, game)
        val playerToLeft = HumanPlayer(User().apply { username = "Bob" }, game)
        game.players.add(player)
        game.players.add(playerToLeft)
        player.hand.clear()
        playerToLeft.hand.clear()
        val revealedCard = Copper()
        player.hand.add(revealedCard)

        Gladiator().onCardChosen(player, revealedCard)

        assertEquals(1, player.availableCoins)
        assertNull(playerToLeft.currentAction)
    }

    private fun newGame(): Game {
        return Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
    }
}
