package com.kingdom.model.players.bots

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Event
import com.kingdom.model.cards.Landmark
import com.kingdom.model.cards.adventures.events.Seaway
import com.kingdom.model.cards.base.Chapel
import com.kingdom.model.cards.base.Gardens
import com.kingdom.model.cards.base.Market
import com.kingdom.model.cards.base.Militia
import com.kingdom.model.cards.base.Sentry
import com.kingdom.model.cards.base.Witch
import com.kingdom.model.cards.empires.events.Delve
import com.kingdom.model.cards.empires.landmarks.Orchard
import com.kingdom.model.cards.intrigue.Minion
import com.kingdom.model.cards.menagerie.Horse
import com.kingdom.model.cards.menagerie.Paddock
import com.kingdom.model.cards.prosperity.Goons
import com.kingdom.model.cards.seaside.Wharf
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Duchy
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Province
import com.kingdom.model.cards.supply.Silver
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
    fun greeningScoresBeatGoldWhenTheyAreEnabled() {
        val duchyBot = hardBot(6, emptyList(), provincesLeft = 5)
        val estateBot = hardBot(6, emptyList(), provincesLeft = 2)

        assertTrue(duchyBot.getBuyCardScore(Duchy()) > duchyBot.getBuyCardScore(Gold()))
        assertTrue(estateBot.getBuyCardScore(Estate()) > estateBot.getBuyCardScore(Gold()))
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
    fun buysFiveCostPayloadOverSilverEvenWithoutNamedStrategy() {
        val bot = hardBot(5, listOf(Wharf()))

        assertEquals(Wharf.NAME, bot.getCardToBuy())
    }

    @Test
    fun buysStrongPayloadOverGoldWhenTheBoardSupportsIt() {
        val bot = hardBot(6, listOf(Wharf()))

        assertEquals(Wharf.NAME, bot.getCardToBuy())
    }

    @Test
    fun buysGoldOnMoneyBoardsWithoutPayload() {
        val bot = hardBot(6, emptyList())

        assertEquals(Gold.NAME, bot.getCardToBuy())
    }

    @Test
    fun keepsBuyScoresOnComparableScale() {
        val bot = hardBot(8, listOf(Wharf()))
        val wharfScore = bot.getBuyCardScore(Wharf())

        assertTrue(bot.getBuyCardScore(Silver()) < wharfScore)
        assertTrue(bot.getBuyCardScore(Province()) < wharfScore * 2)
    }

    @Test
    fun buysFiveCostDeckControlOverSilver() {
        val bot = hardBot(5, listOf(Sentry()))

        assertEquals(Sentry.NAME, bot.getCardToBuy())
    }

    @Test
    fun doesNotBuyHorseFromNonSupplyPile() {
        val bot = hardBot(3, listOf(Paddock()))

        assertFalse(bot.canBuyCard(Horse()))
        assertEquals(Silver.NAME, bot.getCardToBuy())
    }

    @Test
    fun skipsDelveWhenItWouldTradeGoldForSilver() {
        val bot = hardBot(6, emptyList(), events = listOf(Delve()))

        assertEquals(0, bot.getBuyEventScore(Delve()))
        assertEquals(Gold.NAME, bot.getCardToBuy())
    }

    @Test
    fun valuesSeawayWhenItGainsAUsefulActionAndBuyToken() {
        val bot = hardBot(5, listOf(Militia()), events = listOf(Seaway()))

        assertTrue(bot.getBuyEventScore(Seaway()) > bot.getBuyCardScore(Militia()))
    }

    @Test
    fun landmarkScoringPushesTowardOrchardThresholds() {
        val botWithoutOrchard = hardBot(5, listOf(Market()))
        val botWithOrchard = hardBot(5, listOf(Market()), landmarks = listOf(Orchard()))
        repeat(2) {
            botWithoutOrchard.deck.add(Market())
            botWithOrchard.deck.add(Market())
        }

        assertTrue(botWithOrchard.getBuyCardScore(Market()) >= botWithoutOrchard.getBuyCardScore(Market()) + 12)
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

    private fun hardBot(
            coins: Int,
            kingdomCards: List<Card>,
            provincesLeft: Int = 8,
            events: List<Event> = emptyList(),
            landmarks: List<Landmark> = emptyList()
    ): HardBotPlayer {
        val game = Game(GameManager(), GameMessageService(mock(SimpMessagingTemplate::class.java)))
        game.numPlayers = 2
        game.kingdomCards = kingdomCards.toMutableList()
        game.events = events.toMutableList()
        game.landmarks = landmarks.toMutableList()
        game.setupGame()
        game.setupAmountForPile(Province.NAME, provincesLeft)

        val user = User().apply { username = "Hard Bot" }
        val bot = TestHardBotPlayer(user, game)
        bot.chatColor = "red"
        game.players.add(bot)
        bot.enterBuyPhaseForTesting()
        bot.addCoins(coins, refresh = false)

        return bot
    }

    private class TestHardBotPlayer(user: User, game: Game) : HardBotPlayer(user, game) {
        fun enterBuyPhaseForTesting() {
            isYourTurn = true
            addBuys(1, refresh = false)
        }
    }
}
