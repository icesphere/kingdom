package com.kingdom.model

import com.kingdom.model.cards.actions.UndoApprovalAction
import com.kingdom.model.cards.actions.UndoWaitingAction
import com.kingdom.model.cards.actions.ActionResult
import com.kingdom.model.cards.base.Smithy
import com.kingdom.model.cards.base.Workshop
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.players.HumanPlayer
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class GameUndoTests {

    @Test
    fun `undo restores a simple stable command without approval`() {
        val game = testGame()
        val player = addHumanPlayer(game, "Alice")
        player.startTurn(false)
        val copper = resetHandToCopper(player)

        game.beforeUndoableCommand(player, "played Copper")
        player.playCard(copper)
        game.afterUndoableCommand()

        assertTrue(game.canUndoFor(player))
        assertTrue(game.requestUndo(player))

        val restoredPlayer = game.playerMap[player.userId]!!
        assertEquals(1, restoredPlayer.hand.count { it.name == Copper.NAME })
        assertTrue(restoredPlayer.inPlay.isEmpty())
        assertEquals(0, restoredPlayer.availableCoins)
        assertFalse(game.canUndoFor(restoredPlayer))
    }

    @Test
    fun `shared reveal requires approval before restore`() {
        val game = testGame()
        val alice = addHumanPlayer(game, "Alice")
        val bob = addHumanPlayer(game, "Bob")
        alice.startTurn(false)
        val copper = resetHandToCopper(alice)

        game.beforeUndoableCommand(alice, "revealed and played Copper")
        alice.revealHand()
        alice.playCard(copper)
        game.afterUndoableCommand()

        assertTrue(game.requestUndo(alice))
        assertTrue(alice.currentAction is UndoWaitingAction)
        assertTrue(bob.currentAction is UndoApprovalAction)
        assertEquals(1, alice.inPlay.size)

        game.recordUndoApproval(bob, true)

        val restoredAlice = game.playerMap[alice.userId]!!
        val restoredBob = game.playerMap[bob.userId]!!
        assertEquals(1, restoredAlice.hand.count { it.name == Copper.NAME })
        assertTrue(restoredAlice.inPlay.isEmpty())
        assertNull(restoredAlice.currentAction)
        assertNull(restoredBob.currentAction)
    }

    @Test
    fun `rejected reveal undo leaves state unchanged and clears prompts`() {
        val game = testGame()
        val alice = addHumanPlayer(game, "Alice")
        val bob = addHumanPlayer(game, "Bob")
        alice.startTurn(false)
        val copper = resetHandToCopper(alice)

        game.beforeUndoableCommand(alice, "revealed and played Copper")
        alice.revealHand()
        alice.playCard(copper)
        game.afterUndoableCommand()

        assertTrue(game.requestUndo(alice))
        game.recordUndoApproval(bob, false)

        assertTrue(alice.hand.isEmpty())
        assertEquals(1, alice.inPlay.size)
        assertNull(alice.currentAction)
        assertNull(bob.currentAction)
        assertFalse(game.canUndoFor(alice))
    }

    @Test
    fun `undo becomes available after gain prompt settles`() {
        val game = testGame()
        val alice = addHumanPlayer(game, "Alice")
        addHumanPlayer(game, "Bob")
        alice.startTurn(false)
        val workshop = Workshop()
        alice.hand.clear()
        alice.hand.add(workshop)
        game.addKingdomCardPile(Estate())

        game.beforeUndoableCommand(alice, "played Workshop")
        alice.playCard(workshop)
        game.afterUndoableCommand()

        assertFalse(game.canUndoFor(alice))

        alice.actionResult(alice.currentAction!!, ActionResult().apply { selectedCard = Estate() })
        game.afterUndoableCommand()

        assertTrue(game.canUndoFor(alice))
        assertTrue(game.requestUndo(alice))

        val restoredAlice = game.playerMap[alice.userId]!!
        assertEquals(1, restoredAlice.hand.count { it.name == Workshop.NAME })
        assertTrue(restoredAlice.cardsInDiscard.none { it.name == Estate.NAME })
    }

    @Test
    fun `drawn cards require approval before undo`() {
        val game = testGame()
        val alice = addHumanPlayer(game, "Alice")
        val bob = addHumanPlayer(game, "Bob")
        alice.startTurn(false)
        val smithy = Smithy()
        alice.hand.clear()
        alice.hand.add(smithy)
        alice.deck.clear()
        alice.deck.addAll(listOf(Copper(), Copper(), Copper()))

        game.beforeUndoableCommand(alice, "played Smithy")
        alice.playCard(smithy)
        game.afterUndoableCommand()

        assertTrue(game.requestUndo(alice))
        assertTrue(alice.currentAction is UndoWaitingAction)
        assertTrue(bob.currentAction is UndoApprovalAction)
        assertEquals(3, alice.hand.count { it.name == Copper.NAME })
    }

    @Test
    fun `undo is unavailable after turn passes to another player`() {
        val game = testGame()
        val alice = addHumanPlayer(game, "Alice")
        val bob = addHumanPlayer(game, "Bob")
        alice.startTurn(false)
        val copper = resetHandToCopper(alice)

        game.beforeUndoableCommand(alice, "played Copper")
        alice.playCard(copper)
        game.afterUndoableCommand()

        assertTrue(game.canUndoFor(alice))

        setCurrentPlayerIndex(game, game.players.indexOf(bob))

        assertFalse(game.canUndoFor(alice))
        assertFalse(game.requestUndo(alice))
    }

    private fun testGame(): Game {
        return Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java))).apply {
            status = GameStatus.InProgress
            numPlayers = 2
        }
    }

    private fun addHumanPlayer(game: Game, username: String): HumanPlayer {
        val user = User().apply { this.username = username }
        val player = HumanPlayer(user, game)
        game.players.add(player)
        game.playerMap[user.userId] = player
        return player
    }

    private fun resetHandToCopper(player: HumanPlayer): Copper {
        player.hand.clear()
        player.deck.clear()
        player.cardsInDiscard.toMutableList().forEach { player.removeCardFromDiscard(it) }
        val copper = Copper()
        player.hand.add(copper)
        return copper
    }

    private fun setCurrentPlayerIndex(game: Game, index: Int) {
        val field = Game::class.java.getDeclaredField("currentPlayerIndex")
        field.isAccessible = true
        field.setInt(game, index)
    }
}
