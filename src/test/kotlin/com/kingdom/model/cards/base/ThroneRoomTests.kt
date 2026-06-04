package com.kingdom.model.cards.base

import com.kingdom.model.Game
import com.kingdom.model.GameStatus
import com.kingdom.model.User
import com.kingdom.model.cards.actions.ActionResult
import com.kingdom.model.cards.seaside.Caravan
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.players.HumanPlayer
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class ThroneRoomTests {

    @Test
    fun `throne room keeps nested throne room duration chain in play`() {
        val game = testGame()
        val player = addHumanPlayer(game, "Alice")
        val outerThroneRoom = ThroneRoom()
        val innerThroneRoom = ThroneRoom()
        val caravan = Caravan()

        player.startTurn(false)
        player.hand.clear()
        player.deck.clear()
        player.hand.addAll(listOf(outerThroneRoom, innerThroneRoom, caravan))

        player.playCard(outerThroneRoom)
        choose(player, innerThroneRoom)
        choose(player, caravan)

        assertNull(player.currentAction)
        assertEquals(listOf(innerThroneRoom), outerThroneRoom.cardsBeingRepeated)
        assertEquals(listOf(caravan), innerThroneRoom.cardsBeingRepeated)

        player.numCardsToDrawAtEndOfTurn = 0
        player.deck.addAll(listOf(Copper(), Copper()))
        player.endTurn()

        assertTrue(player.durationCards.contains(outerThroneRoom))
        assertTrue(player.durationCards.contains(innerThroneRoom))
        assertTrue(player.durationCards.contains(caravan))
        assertEquals(2, player.hand.count { it.name == Copper.NAME })
    }

    private fun choose(player: HumanPlayer, card: com.kingdom.model.cards.Card) {
        player.actionResult(player.currentAction!!, ActionResult().apply { selectedCard = card })
    }

    private fun testGame(): Game {
        return Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java))).apply {
            status = GameStatus.InProgress
            numPlayers = 1
            setMaxHistoryTurnSize(this, 2)
        }
    }

    private fun setMaxHistoryTurnSize(game: Game, size: Int) {
        val field = Game::class.java.getDeclaredField("maxHistoryTurnSize")
        field.isAccessible = true
        field.setInt(game, size)
    }

    private fun addHumanPlayer(game: Game, username: String): HumanPlayer {
        val user = User().apply { this.username = username }
        val player = HumanPlayer(user, game)
        game.players.add(player)
        game.playerMap[user.userId] = player
        return player
    }
}
