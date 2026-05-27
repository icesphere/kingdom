package com.kingdom.model.players.bots

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.base.Chapel
import com.kingdom.model.cards.base.Gardens
import com.kingdom.model.cards.base.Witch
import com.kingdom.model.cards.intrigue.Minion
import com.kingdom.model.cards.prosperity.Goons
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Duchy
import com.kingdom.model.cards.supply.Province
import com.kingdom.model.cards.supply.Silver
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate

class HardBotPlayerTests {

    @Test
    fun buysChapelOnLowOpeningWhenTheDeckStillHasJunk() {
        val bot = hardBot(2, listOf(Chapel()))

        assertEquals(Chapel.NAME, bot.getCardToBuy())
    }

    @Test
    fun delaysDuchyUntilTheProvincePileRunsDown() {
        val earlyBot = hardBot(5, emptyList(), provincesLeft = 8)
        val lateBot = hardBot(5, emptyList(), provincesLeft = 5)

        assertEquals(Silver.NAME, earlyBot.getCardToBuy())
        assertEquals(Duchy.NAME, lateBot.getCardToBuy())
    }

    @Test
    fun buysASecondWitchButRespectsTheSimulatedDominionCap() {
        val bot = hardBot(5, listOf(Witch()))

        bot.deck.add(Witch())
        assertEquals(Witch.NAME, bot.getCardToBuy())

        bot.deck.add(Witch())
        assertNotEquals(Witch.NAME, bot.getCardToBuy())
    }

    @Test
    fun prioritizesScoringPayloadOverRawTreasure() {
        val bot = hardBot(6, listOf(Goons()))

        assertEquals(Goons.NAME, bot.getCardToBuy())
    }

    @Test
    fun pursuesAlternativeVictoryPointsWhenTheyAreUsable() {
        val bot = hardBot(4, listOf(Gardens()))
        repeat(20) { bot.deck.add(Copper()) }

        assertEquals(Gardens.NAME, bot.getCardToBuy())
    }

    @Test
    fun minionUsesMoneyWhenAnotherMinionIsStillInHand() {
        val bot = hardBot(0, listOf(Minion()))
        bot.inPlay.add(Minion())
        bot.hand.add(Minion())

        val choice = bot.getChoice(Minion(), arrayOf(Choice(1, "+\$2"), Choice(2, "Discard hands")), null)

        assertEquals(1, choice)
    }

    @Test
    fun minionCyclesWhenTheMoneyChoiceWillMissTheTarget() {
        val bot = hardBot(0, listOf(Minion()))
        bot.inPlay.add(Minion())
        bot.hand.clear()

        val choice = bot.getChoice(Minion(), arrayOf(Choice(1, "+\$2"), Choice(2, "Discard hands")), null)

        assertEquals(2, choice)
    }

    private fun hardBot(coins: Int, kingdomCards: List<Card>, provincesLeft: Int = 8): HardBotPlayer {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        game.numPlayers = 2
        game.kingdomCards = kingdomCards.toMutableList()
        game.setupGame()
        game.setupAmountForPile(Province.NAME, provincesLeft)

        val user = User().apply { username = "Hard Bot" }
        val bot = HardBotPlayer(user, game)
        bot.chatColor = "red"
        game.players.add(bot)
        bot.addCoins(coins, refresh = false)

        return bot
    }
}
