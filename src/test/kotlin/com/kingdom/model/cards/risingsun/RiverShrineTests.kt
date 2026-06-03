package com.kingdom.model.cards.risingsun

import com.kingdom.model.Game
import com.kingdom.model.PlayerTurn
import com.kingdom.model.User
import com.kingdom.model.cards.actions.ActionResult
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefit
import com.kingdom.model.cards.base.Workshop
import com.kingdom.model.players.HumanPlayer
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class RiverShrineTests {

    @Test
    fun `cleanup gain ends turn after action resolves`() {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        val alice = HumanPlayer(User().apply { username = "Alice" }, game)
        val bob = HumanPlayer(User().apply { username = "Bob" }, game)
        game.players.addAll(listOf(alice, bob))
        game.playerMap[alice.userId] = alice
        game.playerMap[bob.userId] = bob
        game.recentTurnHistory.add(PlayerTurn(alice))
        game.addKingdomCardPile(Workshop())

        alice.startTurn(false)
        alice.hand.clear()
        alice.hand.add(RiverShrine())
        alice.playCard(alice.hand.first())

        alice.endTurn()
        assertTrue(alice.currentAction is FreeCardFromSupplyForBenefit)

        alice.actionResult(alice.currentAction!!, ActionResult().apply { selectedCard = Workshop() })

        assertNull(alice.currentAction)
        assertFalse(alice.isYourTurn)
        assertTrue(bob.isYourTurn)
        assertTrue(alice.cardsInDiscard.any { it.name == Workshop.NAME })
    }
}
