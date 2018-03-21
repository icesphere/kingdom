package com.kingdom.model.computer

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.*
import com.kingdom.util.CardCostComparator
import com.kingdom.util.KingdomUtil
import com.kingdom.util.computercardaction.*

import java.util.*

abstract class ComputerPlayer(var player: OldPlayer, var game: OldGame) {
    var stopped: Boolean = false

    private var playAction = true
    private var potionsBought = 0

    protected var trashingStrategy = false
    protected var bigMoneyStrategy = false
    private var bigActionsStrategy = false
    protected var victoryCoinsStrategy = false

    var isGardensStrategy = false
    protected var chapelStrategy = false
    protected var dukeStrategy = false
    private var cityStrategy = false
    private var pirateShipStrategy = false
    private var ambassadorStrategy = false
    private var laboratoryStrategy = false
    private var haremStrategy = false
    protected var miningVillageStrategy = false

    protected var kingdomCardMap: Map<String, Card> = game.kingdomCardMap

    private var duchiesBought = 0
    private var dukesBought = 0
    private var kingsCourtsBought = 0
    private var throneRoomsBought = 0
    private var cardsGained: MutableMap<String, Int> = HashMap()
    private var playAllTreasureCards = true
    private var actionsBought = 0
    private var treasureCardsBought = 0
    private var silversBought = 0
    var goldsBought = 0
    private var platinumsBought = 0
    private var terminalActionsBought = 0
    private var checkPeddler = false
    var difficulty = 3
    var isBigMoneyUltimate = false
    private var isHasCountingHouse = false
    private var isHasGoons = false
    protected var hasGardens = false
    protected var hasDuke = false
    private var hasVineyard = false
    protected var firstCard: Card? = null
    protected var secondCard: Card? = null
    protected var trashingCard: Card? = null
    protected var fiveTwoSplit: Boolean = false
    protected var startingHandCoppers: Int = 0
    protected var hasExtraBuys = false
    protected var hasTrashingCard = false
    private var hasExtraActionsCard = false
    private var hasVictoryCoinsCard = false
    private var hasDefenseCard = false
    private var numAttackCards = 0
    protected var trashingCards: MutableList<Card> = ArrayList()
    private var buyCardAttempts = 0
    var error: Boolean = false

    init {
        startingHandCoppers = player.treasureCards.size
        fiveTwoSplit = startingHandCoppers == 2 || startingHandCoppers == 5

        analyzeKingdomCards(game)

        setupStartingStrategies()
    }

    private fun analyzeKingdomCards(game: OldGame) {
        for (card in game.kingdomCards) {
            if (card.addBuys > 0 || card.name == "Workshop" || card.name == "Ironworks") {
                hasExtraBuys = true
            }
            if (card.addActions >= 2) {
                hasExtraActionsCard = true
            }
            if (card.isTrashingCard && !card.costIncludesPotion && card.name != "Remake" && card.name != "Forge") {
                hasTrashingCard = true
                trashingCards.add(card)
            }
            if (card.isVictoryCoinsCard) {
                hasVictoryCoinsCard = true
            }
            if (card.isDefense) {
                hasDefenseCard = true
            }
            if (card.isAttack) {
                numAttackCards++
            }

            when(card.name) {
                "Harem" -> haremStrategy = true
                "Counting House" -> isHasCountingHouse = true
                "Peddler" -> checkPeddler = true
                "Goons" -> isHasGoons = true
                "Gardens" -> hasGardens = true
                "Duke" -> hasDuke = true
                "Vineyard" -> hasVineyard = true
            }
        }
    }

    protected abstract fun setupStartingStrategies()

    fun doNextAction() {
        if (stopped || game.status != OldGame.STATUS_GAME_IN_PROGRESS) {
            return
        }
        try {
            var loopIterations = 0
            while (player.isShowCardAction && player.oldCardAction!!.type == OldCardAction.TYPE_WAITING_FOR_PLAYERS) {
                if (stopped || game.status != OldGame.STATUS_GAME_IN_PROGRESS) {
                    return
                }
                try {
                    Thread.sleep(1000)
                    loopIterations++
                    //if wait is longer than 10 minutes then throw error and continue
                    if (loopIterations > 600) {
                        val error = GameError(GameError.GAME_ERROR, "Computer has been waiting for over 10 minutes for player to finish card action.")
                        game.logError(error, false)
                        break
                    }
                } catch (e: InterruptedException) {
                    //
                }

            }
            loopIterations = 0
            while (!player.isShowCardAction && game.hasIncompleteCard()) {
                if (stopped || game.status != OldGame.STATUS_GAME_IN_PROGRESS) {
                    return
                }
                try {
                    Thread.sleep(500)
                    loopIterations++
                    //if wait is longer than 15 minutes then throw error and continue
                    if (loopIterations > 1200) {
                        val error = GameError(GameError.GAME_ERROR, "computer-hasIncompleteCard in never ending loop. Incomplete Card: " + game.incompleteCard!!.cardName)
                        game.logError(error, false)
                        break
                    }
                } catch (e: InterruptedException) {
                    //
                }

            }
            if (error) {
                endTurn()
            }
            if (player.isShowCardAction) {
                handleCardAction(player.oldCardAction!!)
                doNextAction()
            } else if (playAction && player.actions > 0 && !player.actionCards.isEmpty()) {
                playAction()
                doNextAction()
            } else if (game.isPlayTreasureCards && !player.treasureCards.isEmpty()) {
                playTreasure()
                doNextAction()
            } else if (player.buys > 0 && player.coins >= 0) {
                val coinsBeforeBuy = player.coins
                val cardBought = buyCard()
                if (cardBought != null) {
                    buyCardAttempts++
                    game.cardClicked(player, "supply", cardBought)

                    val cardBoughtCost = game.getCardCostBuyPhase(cardBought)

                    val expectedCoins = coinsBeforeBuy - cardBoughtCost

                    if (player.coins != expectedCoins) {
                        val error = GameError(GameError.COMPUTER_ERROR, "Card bought: " + cardBought.name + ". Expected coins: " + expectedCoins + ". Actual coins: " + player.coins)
                        game.logError(error, false)
                        endTurn()
                    } else {
                        if (buyCardAttempts > 10) {
                            val error = GameError(GameError.COMPUTER_ERROR, "Computer tried to buy cards more than 10 times. Recent card bought: " + cardBought.name)
                            game.logError(error, false)
                            endTurn()
                        } else {
                            doNextAction()
                        }
                    }
                } else {
                    endTurn()
                }
            } else {
                endTurn()
            }
        } catch (t: Throwable) {
            val error = GameError(GameError.COMPUTER_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

    }

    private fun endTurn() {
        playAllTreasureCards = true
        playAction = true
        buyCardAttempts = 0
        error = false
        game.endPlayerTurn(player)
    }

    fun gainedCard(card: Card) {
        var numBought: Int? = cardsGained[card.name]
        if (numBought == null) {
            numBought = 0
        }
        numBought++
        if (card.isAction) {
            actionsBought++
            if (card.isTerminalAction) {
                terminalActionsBought++
                if (card.name == "Throne Room") {
                    throneRoomsBought++
                } else if (card.name == "King's Court") {
                    kingsCourtsBought++
                }
            }
        }

        if (card.isTreasure && !card.isPotion) {
            treasureCardsBought++
            when {
                card.isSilver -> silversBought++
                card.isGold -> goldsBought++
                card.isPlatinum -> platinumsBought++
            }
        }

        if (dukeStrategy) {
            if (card.name == "Duke") {
                dukesBought++
            } else if (card.name == "Duchy") {
                duchiesBought++
            }
        }
        cardsGained.put(card.name, numBought)
    }

    private fun playAction() {
        var actionToPlay: Card? = null
        if (difficulty >= 2 && player.actionCards.size > 1) {
            for (card in player.actionCards) {
                if (card.name == "Throne Room" || card.name == "King's Court") {
                    actionToPlay = card
                }
            }
            if (actionToPlay != null) {
                var numTimesToPlayAction = 2
                if (actionToPlay.name == "King's Court") {
                    numTimesToPlayAction = 3
                }
                if (getActionToDuplicate(player.actionCards, numTimesToPlayAction) == null) {
                    actionToPlay = null
                }
            }
        }
        if (difficulty >= 2 && actionToPlay == null) {
            for (card in player.actionCards) {
                if (card.isAction && card.addActions > 0) {
                    actionToPlay = card
                    if (card.name != "Shanty Town" && card.name != "Apprentice") {
                        break
                    }
                }
            }
        }

        if (actionToPlay == null) {
            if (difficulty < 2) {
                actionToPlay = player.actionCards[0]
            } else {
                val ccc = CardCostComparator()
                Collections.sort(player.actionCards, Collections.reverseOrder(ccc))
                for (card in player.actionCards) {
                    if (shouldPlayAction(card)) {
                        actionToPlay = card
                        break
                    }
                }
            }
        }

        if (actionToPlay != null) {
            game.cardClicked(player, "hand", actionToPlay)
        } else {
            this.playAction = false
        }
    }

    private fun shouldPlayAction(action: Card): Boolean {
        var playAction = true
        when {
            (action.name == "Apprentice" || action.name == "Chapel" || action.name == "Ambassador" || action.name == "Salvager") && getNumCardsWorthTrashing(player.hand) == 0 -> playAction = false
            action.name == "Chapel" && (goldsBought == 0 && silversBought == 0 || player.coins >= 5 && player.turns < 6) -> playAction = false
            action.name == "Tactician" && player.coins >= 6 -> playAction = false
            action.name == "Trade Route" && getNumCardsWorthTrashing(player.hand) == 0 && game.tradeRouteTokensOnMat < 3 -> playAction = false
            action.name == "Bishop" && getNumCardsWorthTrashing(player.hand) == 0 && !onlyBuyVictoryCards() -> playAction = false
        }
        return playAction
    }

    fun getActionToDuplicate(cards: List<Card>, numTimesToPlayAction: Int): Card? {
        var actionToDuplicate: Card? = null
        for (card in cards) {
            if (card.addActions > 0 && card.name != "Apprentice") {
                actionToDuplicate = card
                break
            }
        }
        if (actionToDuplicate == null) {
            for (card in cards) {
                if (card.name == "Chapel" || card.name == "Throne Room" || card.name == "King's Court") {
                    continue
                }
                if ((card.name == "Ambassador" || card.name == "Apprentice" || card.name == "Trade Route") && getNumCardsWorthTrashing(cards) < numTimesToPlayAction) {
                    continue
                }
                actionToDuplicate = card
                break
            }
        }
        return actionToDuplicate
    }

    private fun playTreasure() {

        //play Contraband cards first
        if (difficulty >= 2 && game.isTrackContrabandCards) {
            for (card in player.treasureCards) {
                if (card.name == "Contraband") {
                    game.cardClicked(player, "hand", card)
                    return
                }
            }
        }

        if (playAllTreasureCards) {
            playAllTreasureCards = false
            game.playAllTreasureCards(player, false)
        } else {
            var treasureToPlay: Card? = null

            //play Bank cards last
            if (difficulty >= 2 && game.isTrackBankCards) {
                for (card in player.treasureCards) {
                    if (card.name != "Bank") {
                        treasureToPlay = card
                        break
                    }
                }
            }
            if (treasureToPlay == null) {
                treasureToPlay = player.treasureCards[0]
            }
            game.cardClicked(player, "hand", treasureToPlay)
        }
    }

    fun onlyBuyVictoryCards(): Boolean {
        var onlyBuyVictoryCards = false
        val provincesInSupply = game.supply[Province.NAME]
        if (provincesInSupply == null) {
            val error = GameError(GameError.COMPUTER_ERROR, "Supply was null for Province")
            game.logError(error)
        }
        if (game.numPlayers == 2 && game.supply[Province.NAME]!! <= 2 || game.numPlayers > 2 && game.supply[Province.NAME]!! <= 3) {
            onlyBuyVictoryCards = true
        } else if (game.isIncludeColonyCards && (game.numPlayers == 2 && game.supply[Colony.NAME]!! <= 2 || game.numPlayers > 2 && game.supply[Colony.NAME]!! <= 3)) {
            onlyBuyVictoryCards = true
        } else if (difficulty >= 2) {
            var pilesWithOneCard = 0
            var pilesWithTwoCards = 0
            for (numInSupply in game.supply.values) {
                if (numInSupply == 1) {
                    pilesWithOneCard++
                } else if (numInSupply == 2) {
                    pilesWithTwoCards++
                }
            }
            var numEmptyPilesForGameEnd = 3
            if (game.numPlayers > 4) {
                numEmptyPilesForGameEnd = 4
            }
            if (game.numEmptyPiles + pilesWithOneCard + pilesWithTwoCards == numEmptyPilesForGameEnd) {
                onlyBuyVictoryCards = true
            }
        }
        return onlyBuyVictoryCards
    }

    private fun includeVictoryOnlyCards(): Boolean {
        return player.turns > 8
    }

    protected open fun buyCard(): Card? {
        if (player.coins < 2 && !isGardensStrategy && !isHasGoons) {
            return null
        }

        if (game.isUsePotions && player.coins == 4 && potionsBought == 0 && !kingdomCardMap.containsKey("Black Market") && (!game.isShowTrollTokens || game.numTrollTokens(Potion.NAME) == 0)) {
            potionsBought++
            return game.cardMap[Potion.NAME]
        }

        if (chapelStrategy && cardsGained[getKingdomCard("Chapel").name] == null && (player.coins == 3 && silversBought > 0 || player.coins == 2) && (!game.isTrackContrabandCards || !game.contrabandCards.contains(getKingdomCard("Chapel")))) {
            return getKingdomCard("Chapel")
        }
        if (chapelStrategy && player.coins <= 3 && silversBought < 2 && goldsBought == 0 && game.canBuyCard(player, game.silverCard)) {
            return game.silverCard
        }
        if (chapelStrategy && laboratoryStrategy && player.coins <= 5 && game.canBuyCard(player, getKingdomCard("Laboratory"))) {
            return getKingdomCard("Laboratory")
        }
        if (player.coins >= game.getCardCostBuyPhase(game.goldCard) && goldsBought == 0 && (!game.isIncludePlatinumCards || player.coins < game.getCardCostBuyPhase(game.platinumCard)) && game.canBuyCard(player, game.goldCard)) {
            return game.goldCard
        }
        if (isGardensStrategy) {
            if (player.turns > 4 && game.canBuyCard(player, getKingdomCard("Gardens"))) {
                if (onlyBuyVictoryCards() && player.coins >= game.getCardCostBuyPhase(getKingdomCard("Gardens"))) {
                    return getKingdomCard("Gardens")
                } else if (player.coins == game.getCardCostBuyPhase(getKingdomCard("Gardens"))) {
                    return getKingdomCard("Gardens")
                }
            }
        }
        if (dukeStrategy) {
            val duchiesInSupply = game.supply[Duchy.NAME]!!
            if (duchiesInSupply > 0 && (player.turns > 8 || duchiesInSupply <= 6) && player.coins >= game.getCardCostBuyPhase(game.duchyCard)) {
                if (duchiesBought < 4 || duchiesBought <= dukesBought && (!game.isTrackContrabandCards || !game.contrabandCards.contains(game.duchyCard))) {
                    return game.duchyCard
                } else if (duchiesBought >= 3 && game.canBuyCard(player, getKingdomCard("Duke"))) {
                    return getKingdomCard("Duke")
                }
            }
        }
        if (checkPeddler && !onlyBuyVictoryCards() && game.canBuyCard(player, getKingdomCard("Peddler"))) {
            if (player.coins < 6) {
                return getKingdomCard("Peddler")
            }
        }
        if (cityStrategy && !onlyBuyVictoryCards() && player.coins <= 6 && player.turns > 8 && game.canBuyCard(player, getKingdomCard("City"))) {
            return getKingdomCard("City")
        }
        if (ambassadorStrategy && !onlyBuyVictoryCards() && player.turns < 2) {
            return getKingdomCard("Ambassador")
        }
        if (pirateShipStrategy && !onlyBuyVictoryCards() && player.coins <= 4 && terminalActionsBought - actionsBought < 2 && game.canBuyCard(player, getKingdomCard("Pirate Ship"))) {
            return getKingdomCard("Pirate Ship")
        }

        var cardToBuy = getRandomHighestCostCardFromCostMap(player.coins, false)
        if (player.potions > 0) {
            val potionCardToBuy = getRandomHighestCostCardFromCostMap(player.coins, true)
            if (potionCardToBuy != null && (cardToBuy == null || potionCardToBuy.cost + 3 >= cardToBuy.cost)) {
                cardToBuy = potionCardToBuy
            }
        }
        if (cardToBuy != null) {
            return cardToBuy
        } else if (difficulty >= 2 && wantsCoppers()) {
            return game.copperCard
        }
        return null
    }
    
    private fun getKingdomCard(name: String): Card {
        return kingdomCardMap[name]!!
    }

    protected fun buyCardHardDifficulty(): Card? {
        if (player.turns < 2) {
            if (player.turns == 0 && firstCard != null) {
                return firstCard
            } else if (player.turns == 1 && secondCard != null) {
                return secondCard
            }
        }

        val losingMargin = game.getLosingMargin(player.userId)
        val winning = game.currentlyWinning(player.userId)

        if (onlyBuyVictoryCards() && (winning || losingMargin < 6)) {
            for (cardName in game.supply.keys) {
                val card = game.supplyMap[cardName]!!
                if (game.buyingCardWillEndGame(cardName) && game.canBuyCard(player, card)
                        && (winning || card.victoryPoints > losingMargin)) {
                    return card
                }
            }
        }

        if (game.isIncludeColonyCards && player.coins >= game.getCardCostBuyPhase(game.colonyCard) && game.canBuyCard(player, game.colonyCard)) {
            if (!game.buyingCardWillEndGame(Colony.NAME) || game.currentlyWinning(player.userId) || losingMargin < 10) {
                return game.colonyCard
            }
        }

        if (game.isIncludePlatinumCards && platinumsBought <= 1 && !onlyBuyVictoryCards() && player.coins >= game.getCardCostBuyPhase(game.platinumCard) && game.canBuyCard(player, game.platinumCard)) {
            if (!game.buyingCardWillEndGame(Platinum.NAME) || game.currentlyWinning(player.userId)) {
                return game.platinumCard
            }
        }

        if (isGardensStrategy && player.turns > 4 && game.canBuyCard(player, getKingdomCard("Gardens"))) {
            val gardenCost = game.getCardCostBuyPhase(getKingdomCard("Gardens"))
            if (onlyBuyVictoryCards() && player.coins >= gardenCost && (player.coins < game.getCardCostBuyPhase(game.provinceCard) || player.buys > 1)) {
                return getKingdomCard("Gardens")
            } else if (player.coins == gardenCost) {
                return getKingdomCard("Gardens")
            }
        }

        if (goldsBought >= 1 && player.coins >= game.getCardCostBuyPhase(game.provinceCard) && game.canBuyCard(player, game.provinceCard)) {
            if (!game.buyingCardWillEndGame(Province.NAME) || game.currentlyWinning(player.userId) || losingMargin < 6) {
                return game.provinceCard
            }
        }

        if (haremStrategy && goldsBought >= 2 && player.coins >= game.getCardCostBuyPhase(game.goldCard) && game.canBuyCard(player, getKingdomCard("Harem"))) {
            if (!game.buyingCardWillEndGame("Harem") || game.currentlyWinning(player.userId) || losingMargin < 2) {
                return getKingdomCard("Harem")
            }
        }

        if (kingdomCardMap.containsKey("Farmland") && game.supply[Province.NAME]!! <= 5 && player.coins >= game.getCardCostBuyPhase(getKingdomCard("Farmland")) && game.canBuyCard(player, getKingdomCard("Farmland"))) {
            if (!game.buyingCardWillEndGame("Farmland") || game.currentlyWinning(player.userId) || losingMargin < 2) {
                return getKingdomCard("Farmland")
            }
        }

        if (game.supply[Province.NAME]!! <= 5 && player.coins >= game.getCardCostBuyPhase(game.duchyCard) && game.canBuyCard(player, game.duchyCard)) {
            if (!game.buyingCardWillEndGame(Duchy.NAME) || game.currentlyWinning(player.userId) || losingMargin < 3) {
                return game.duchyCard
            }
        }

        if (kingdomCardMap.containsKey("Nobles") && game.supply[Province.NAME]!! <= 3 && player.coins >= game.getCardCostBuyPhase(getKingdomCard("Nobles")) && game.canBuyCard(player, getKingdomCard("Nobles"))) {
            if (!game.buyingCardWillEndGame("Nobles") || game.currentlyWinning(player.userId) || losingMargin < 2) {
                return getKingdomCard("Nobles")
            }
        }

        if (kingdomCardMap.containsKey("Island") && game.supply[Province.NAME]!! <= 2 && player.coins >= game.getCardCostBuyPhase(getKingdomCard("Island")) && game.canBuyCard(player, getKingdomCard("Island"))) {
            if (!game.buyingCardWillEndGame("Island") || game.currentlyWinning(player.userId) || losingMargin < 2) {
                return getKingdomCard("Island")
            }
        }

        if (kingdomCardMap.containsKey("Great Hall") && game.supply[Province.NAME]!! <= 2 && player.coins >= game.getCardCostBuyPhase(getKingdomCard("Great Hall")) && game.canBuyCard(player, getKingdomCard("Great Hall"))) {
            if (!game.buyingCardWillEndGame("Great Hall") || game.currentlyWinning(player.userId)) {
                return getKingdomCard("Great Hall")
            }
        }

        if (game.supply[Province.NAME]!! <= 2 && player.coins >= game.getCardCostBuyPhase(game.estateCard) && game.canBuyCard(player, game.estateCard)) {
            if (!game.buyingCardWillEndGame(Estate.NAME) || game.currentlyWinning(player.userId) || losingMargin < 2) {
                return game.estateCard
            }
        }

        if (bigMoneyStrategy && actionsBought >= 3) {
            return buyCardBigMoneyUltimate()
        }

        if (trashingStrategy && cardsGained[trashingCard!!.name] == null && player.coins == game.getCardCostBuyPhase(trashingCard!!) && game.canBuyCard(player, trashingCard!!)) {
            return trashingCard
        }

        if (player.turns < 2) {
            if (terminalActionsBought > 0) {
                val cardsWithActions = ArrayList<Card>()
                for (card in game.kingdomCards) {
                    if (card.addActions > 0 && card.cost == player.coins && !excludeCardDefault(card) && !card.costIncludesPotion) {
                        cardsWithActions.add(card)
                    }
                }
                if (player.coins > 2) {
                    cardsWithActions.add(game.silverCard)
                    cardsWithActions.add(game.silverCard)
                }
                if (!cardsWithActions.isEmpty()) {
                    Collections.shuffle(cardsWithActions)
                    return cardsWithActions[0]
                }
            }
        }

        return null
    }

    protected fun buyCardBigMoneyUltimate(): Card? {

        if (player.coins >= 11 && game.isIncludeColonyCards && game.canBuyCard(player, game.colonyCard)) {
            return game.colonyCard
        }

        if (game.isIncludePlatinumCards && game.isIncludeColonyCards && player.turns < 10 && platinumsBought == 0 && player.coins >= 9 && game.canBuyCard(player, game.platinumCard)) {
            return game.platinumCard
        }

        if (player.coins >= 8 && game.canBuyCard(player, game.provinceCard)) {
            return game.provinceCard
        }

        if (game.supply[Province.NAME]!! <= 5 && player.coins >= 5 && game.canBuyCard(player, game.duchyCard)) {
            return game.duchyCard
        }

        if (kingdomCardMap.containsKey("Island") && game.supply[Province.NAME]!! <= 2 && player.coins >= 4 && game.canBuyCard(player, getKingdomCard("Island"))) {
            return getKingdomCard("Island")
        }

        if (kingdomCardMap.containsKey("Great Hall") && game.supply[Province.NAME]!! <= 2 && player.coins >= 3 && game.canBuyCard(player, getKingdomCard("Great Hall"))) {
            return getKingdomCard("Great Hall")
        }

        if (game.supply[Province.NAME]!! <= 2 && player.coins >= 2 && game.canBuyCard(player, game.estateCard)) {
            return game.estateCard
        }

        if (player.coins >= 6 && game.canBuyCard(player, game.goldCard)) {
            return game.goldCard
        }

        return if (player.coins >= 3 && game.canBuyCard(player, game.silverCard)) {
            game.silverCard
        } else null

    }

    fun getRandomHighestCostCardFromCostMap(cost: Int, costIncludesPotion: Boolean): Card? {
        var cardToGain: Card? = null
        val costMap: Map<Int, List<Card>> = if (costIncludesPotion) {
            game.potionCostMap
        } else {
            game.costMap
        }
        var adjustedCost = cost
        if (game.costDiscount > 0) {
            adjustedCost += game.costDiscount
        }
        if (game.actionCardDiscount > 0) {
            adjustedCost += game.actionCardDiscount
        }
        for (i in adjustedCost downTo 2) {
            val cards = costMap[i]
            if (cards != null) {
                val availableCards = ArrayList<Card>()
                for (card in cards) {
                    val numInSupply = game.supply[card.name]
                    if (numInSupply == null) {
                        val error = GameError(GameError.COMPUTER_ERROR, "Supply was null for " + card.name)
                        game.logError(error, false)
                        continue
                    }
                    if (!game.canBuyCard(player, card)) {
                        continue
                    }
                    if (onlyBuyVictoryCards()) {
                        if (card.isVictory) {
                            availableCards.add(card)
                        }
                    } else if (!excludeCardDefault(card)) {
                        availableCards.add(card)
                    }
                }
                if (!availableCards.isEmpty()) {
                    Collections.shuffle(availableCards)
                    cardToGain = availableCards[0]
                    break
                }
            }
        }
        return cardToGain
    }

    private fun excludeCardDefault(card: Card): Boolean {
        if (card.costIncludesPotion && player.potions == 0) {
            return true
        }

        if (card.name == "Grand Market") {
            for (treasureCard in game.treasureCardsPlayed) {
                if (treasureCard.isCopper) {
                    return true
                }
            }
        }

        return excludeCard(card)
    }

    protected abstract fun excludeCard(card: Card): Boolean

    protected fun excludeCardEasy(card: Card): Boolean {
        return when {
            card.isCurseOnly -> true
            card.name == "Chapel" -> true
            card.name == "Black Market" -> true
            card.name == "Treasure Map" -> true
            card.name == "Museum" -> true
            card.name == "Archivist" -> true
            else -> false
        }
    }

    protected fun excludeCardMedium(card: Card): Boolean {
        if (excludeCardEasy(card)) {
            return true
        }

        if (game.isShowEmbargoTokens ) {
            val embargoTokens = game.embargoTokens[card.name]!!
            if (embargoTokens > 0) {
                if (embargoTokens > 2 || !card.isProvince && !card.isColony) {
                    return true
                }
            }
        }
        if (!card.isVictory && !card.isTreasure && card.cost < 5 && cardsGained[card.name] != null && cardsGained[card.name]!! >= 3) {
            return true
        } else if (chapelStrategy && card.name == "Counting House") {
            return true
        } else if (card.name == "Monk") {
            return true
        } else if (card.name == "Baptistry" && wantsCoppers()) {
            return true
        } else if (card.isPotion && potionsBought > 0) {
            return true
        } else if (card.name == "Throne Room" && (player.turns < 3 || throneRoomsBought >= 2)) {
            return true
        } else if (card.name == "King's Court" && kingsCourtsBought >= 2) {
            return true
        } else if (card.isTerminalAction && (terminalActionsBought - actionsBought > 1 || terminalActionsBought == 1 && actionsBought == 1)) {
            return true
        } else if (card.name == "Lookout") {
            return true
        } else if (card.isVictoryOnly && !includeVictoryOnlyCards()) {
            return true
        } else if (player.turns <= 5 && card.name == "Great Hall") {
            return true
        } else if (card.name == "Forge") {
            return true
        } else if (card.name == "Witch" && player.turns >= 8) {
            return true
        } else if (card.name == "Mint" && player.turns >= 5) {
            return true
        } else if (card.name == "Remake") {
            return true
        } else if (card.name == "Sorceress") {
            return true
        } else if (card.name == "Outpost") {
            return true
        } else if (card.name == "Quest") {
            return true
        } else if (card.isPotion && kingdomCardMap.containsKey("Black Market")) {
            return true
        } else if (card.name == "Rancher") {
            return true
        } else if (card.name == "Farmland" && player.hand.all { it.isProvince }) {
            return true
        }

        return false
    }

    protected fun excludeCardHard(card: Card): Boolean {
        if (excludeCardMedium(card)) {
            return true
        }

        if (game.buyingCardWillEndGame(card.name) && !game.currentlyWinning(player.userId)) {
            return true
        } else if (card.isVictoryOnly) {
            return true
        } else if (card.name == "Mine" || card.name == "Thief" || card.name == "Chancellor"
                || card.name == "Wishing Well" || card.name == "Workshop"
                || card.name == "Horn of Plenty" || card.name == "Quarry"
                || card.name == "Trader" || card.name == "Navigator"
                || card.name == "Oracle" || card.name == "Fool's Gold") {
            return true
        } else if (card.name == "Workshop" && !isGardensStrategy) {
            return true
        } else if (player.turns < 2 && card.name == "Village") {
            return true
        } else if (bigMoneyStrategy && card.isTerminalAction && terminalActionsBought - actionsBought > 0) {
            return true
        } else if (bigMoneyStrategy && card.isAction && !card.isVictory && actionsBought >= 3) {
            return true
        } else if (!bigActionsStrategy && card.isAction && !card.isVictory && actionsBought >= 5) {
            return true
        } else if (card.name == "Expand" && cardsGained["Expand"] != null) {
            return true
        } else if (bigMoneyStrategy && card.isExtraActionsCard) {
            return true
        } else if (!isGardensStrategy && card.name == "Talisman") {
            return true
        }

        return false
    }

    fun getLowestCostCard(cards: List<Card>?): Card? {
        if (cards == null || cards.isEmpty()) {
            return null
        }
        if (cards.size == 1) {
            return cards[0]
        }
        val ccc = CardCostComparator()
        Collections.sort(cards, ccc)
        val lowCards = ArrayList<Card>()
        var lowestCost = cards[0].cost
        if (cards[0].costIncludesPotion) {
            lowestCost += 2
        }
        for (card in cards) {
            var cost = card.cost
            if (card.costIncludesPotion) {
                cost += 2
            }
            if (cost == lowestCost) {
                if (!excludeCardDefault(card)) {
                    lowCards.add(card)
                }
            } else {
                break
            }
        }
        if (lowCards.isEmpty()) {
            for (card in cards) {
                if (!excludeCardDefault(card)) {
                    return card
                }
            }
            return cards[0]
        }
        Collections.shuffle(lowCards)
        return lowCards[0]
    }

    @JvmOverloads
    fun getHighestCostCard(cards: List<Card>?, includeVictoryOnlyCards: Boolean = true): Card? {
        if (cards == null || cards.isEmpty()) {
            return null
        }
        if (cards.size == 1) {
            return cards[0]
        }
        val ccc = CardCostComparator()
        Collections.sort(cards, Collections.reverseOrder(ccc))
        val topCards = ArrayList<Card>()
        var highestCost = cards[0].cost
        if (cards[0].costIncludesPotion) {
            highestCost += 2
        }
        if (includeVictoryOnlyCards && onlyBuyVictoryCards()) {
            for (card in cards) {
                if (card.isVictory) {
                    return card
                }
            }
        }
        for (card in cards) {
            var cost = card.cost
            if (card.costIncludesPotion) {
                cost += 2
            }
            if (cost == highestCost) {
                if (!excludeCardDefault(card) && (includeVictoryOnlyCards || !card.isVictoryOnly)) {
                    topCards.add(card)
                }
            } else {
                break
            }
        }
        if (topCards.isEmpty()) {
            for (card in cards) {
                if (!excludeCardDefault(card)) {
                    return card
                }
            }
            return cards[0]
        }
        Collections.shuffle(topCards)
        return topCards[0]
    }

    //todo create method to get all useless action cards

    private fun getUselessAction(cards: List<Card>?): Card? {
        var action: Card? = null
        if (cards != null && !cards.isEmpty()) {
            if (cards.size == 1) {
                val card = cards[0]
                if (card.name == "Throne Room" || card.name == "King's Court") {
                    action = card
                } else if (card.name == "Apprentice" && getNumCardsWorthTrashing(cards) == 0) {
                    action = card
                } else if (card.name == "Bishop" && getNumCardsWorthTrashing(cards) == 0 && !onlyBuyVictoryCards()) {
                    action = card
                }
            } else {
                var hasCardWithActions = false
                for (card in cards) {
                    if (card.name == "Throne Room" || card.name == "King's Court" || card.isAction && card.addActions > 0) {
                        hasCardWithActions = true
                        break
                    }
                }
                if (!hasCardWithActions) {
                    action = cards[0]
                }
            }
        }
        return action
    }

    fun getCardToPutOnTopOfDeck(cards: List<Card>?): Card? {
        if (cards == null || cards.isEmpty()) {
            return null
        }
        var topDeckCard: Card? = null
        for (card in cards) {
            if (card.isCurseOnly) {
                topDeckCard = card
                break
            }
        }
        if (topDeckCard == null) {
            for (card in cards) {
                if (card.isVictoryOnly) {
                    topDeckCard = card
                    break
                }
            }
        }
        if (topDeckCard == null) {
            for (card in cards) {
                if (card.isCopper) {
                    topDeckCard = card
                    break
                }
            }
        }
        if (topDeckCard == null) {
            topDeckCard = getUselessAction(player.actionCards)
        }
        if (topDeckCard == null) {
            Collections.shuffle(cards)
            topDeckCard = cards[0]
        }
        return topDeckCard
    }

    fun getCardToPass(cards: List<Card>): String? {
        return getCardsToTrash(cards, 1)[0]
    }

    fun getNumCardsWorthDiscarding(cards: List<Card>?): Int {
        if (cards == null || cards.isEmpty()) {
            return 0
        }
        var numCardsWorthDiscarding = 0
        for (card in cards) {
            if (isCardToDiscard(card)) {
                numCardsWorthDiscarding++
            }
        }

        if (getUselessAction(cards) != null) {
            numCardsWorthDiscarding++
        }

        return numCardsWorthDiscarding
    }

    fun isCardToDiscard(card: Card): Boolean {
        var shouldDiscard = false
        if (card.isCurseOnly || card.isVictoryOnly || card.isVictoryReaction) {
            shouldDiscard = true
        }
        return shouldDiscard
    }

    fun getNumCardsWorthTrashing(cards: List<Card>?): Int {
        if (cards == null || cards.isEmpty()) {
            return 0
        }
        var numCardsWorthTrashing = 0
        for (card in cards) {
            if (isCardToTrash(card)) {
                numCardsWorthTrashing++
            }
        }
        return numCardsWorthTrashing
    }

    private fun shouldTrashCopper(): Boolean {
        val totalNumCards = player.deck.size + player.discard.size + player.hand.size
        return !wantsCoppers() && (silversBought > 0 || goldsBought > 0 || treasureCardsBought > 2) && totalNumCards > 5
    }

    fun isCardToTrash(card: Card): Boolean {
        var shouldTrash = false
        if (card.isCurseOnly || card.isCopper && shouldTrashCopper() || card.name == Estate.NAME && player.turns <= 10) {
            shouldTrash = true
        }
        return shouldTrash
    }

    @JvmOverloads
    fun getCardsToDiscard(cards: List<Card>, numCardsToDiscard: Int, includeVictoryCards: Boolean = true): List<String> {
        val cardsToDiscard = ArrayList<String>()
        //discard Curses first
        for (card in cards) {
            if (card.isCurseOnly) {
                cardsToDiscard.add(Curse.NAME)
                if (cardsToDiscard.size == numCardsToDiscard) {
                    break
                }
            }
        }
        //next discard victory cards
        if (includeVictoryCards && cardsToDiscard.size < numCardsToDiscard) {
            for (card in cards) {
                if (card.isVictoryOnly) {
                    cardsToDiscard.add(card.name)
                }
                if (cardsToDiscard.size == numCardsToDiscard) {
                    break
                }
            }
        }
        //next discard coppers
        if (cardsToDiscard.size < numCardsToDiscard) {
            for (card in cards) {
                if (card.isCopper) {
                    cardsToDiscard.add(Copper.NAME)
                }
                if (cardsToDiscard.size == numCardsToDiscard) {
                    break
                }
            }
        }
        //next add useless action
        if (cardsToDiscard.size < numCardsToDiscard) {
            val uselessAction = getUselessAction(cards)
            if (uselessAction != null) {
                cardsToDiscard.add(uselessAction.name)
            }
        }
        //next discard lowest cost cards
        if (cardsToDiscard.size < numCardsToDiscard) {
            val extraCards = ArrayList<Card>()
            for (card in cards) {
                if (!cardsToDiscard.contains(card.name)) {
                    extraCards.add(card)
                }
            }

            val ccc = CardCostComparator()
            Collections.sort(extraCards, ccc)
            for (card in extraCards) {
                cardsToDiscard.add(card.name)
                if (cardsToDiscard.size == numCardsToDiscard) {
                    break
                }
            }
        }
        return cardsToDiscard
    }

    fun getCardsToTrash(cards: List<Card>, numCardsToTrash: Int): List<String> {
        val cardsToTrash = ArrayList<String>()
        //trash Curses first
        for (card in cards) {
            if (card.isCurseOnly) {
                cardsToTrash.add(Curse.NAME)
                if (cardsToTrash.size == numCardsToTrash) {
                    break
                }
            }
        }
        //next trash estates
        if (cardsToTrash.size < numCardsToTrash) {
            for (card in cards) {
                if (card.isEstate && player.turns < 10) {
                    cardsToTrash.add(card.name)
                }
                if (cardsToTrash.size == numCardsToTrash) {
                    break
                }
            }
        }
        //next trash coppers
        if (cardsToTrash.size < numCardsToTrash) {
            for (card in cards) {
                if (card.isCopper) {
                    cardsToTrash.add(Copper.NAME)
                }
                if (cardsToTrash.size == numCardsToTrash) {
                    break
                }
            }
        }
        //next trash lowest cost cards
        if (cardsToTrash.size < numCardsToTrash) {
            val extraCards = ArrayList<Card>()
            for (card in cards) {
                if (!cardsToTrash.contains(card.name)) {
                    extraCards.add(card)
                }
            }

            val ccc = CardCostComparator()
            Collections.sort(extraCards, ccc)
            for (card in extraCards) {
                cardsToTrash.add(card.name)
                if (cardsToTrash.size == numCardsToTrash) {
                    break
                }
            }
        }
        return cardsToTrash
    }

    fun getCardsNotNeeded(cards: List<Card>, numCardsNotNeeded: Int): List<String> {
        val cardsNotNeeded = ArrayList<String>()
        val cardsCopy = ArrayList<Card>()
        cardsCopy.addAll(cards)
        val remainingCards = ArrayList<Card>()
        //add Curses first
        for (card in cardsCopy) {
            if (card.isCurseOnly) {
                cardsNotNeeded.add(Curse.NAME)
                if (cardsNotNeeded.size == numCardsNotNeeded) {
                    break
                }
            } else {
                remainingCards.add(card)
            }
        }
        //next add victory cards
        if (cardsNotNeeded.size < numCardsNotNeeded) {
            cardsCopy.clear()
            cardsCopy.addAll(remainingCards)
            remainingCards.clear()
            for (card in cardsCopy) {
                if (card.isVictoryOnly) {
                    cardsNotNeeded.add(card.name)
                    if (cardsNotNeeded.size == numCardsNotNeeded) {
                        break
                    }
                } else {
                    remainingCards.add(card)
                }
            }
        }
        //next add useless actions
        if (cardsNotNeeded.size < numCardsNotNeeded) {
            cardsCopy.clear()
            cardsCopy.addAll(remainingCards)
            remainingCards.clear()
            var uselessAction = getUselessAction(cardsCopy)
            while (uselessAction != null) {
                cardsNotNeeded.add(uselessAction.name)
                if (cardsNotNeeded.size == numCardsNotNeeded) {
                    break
                }
                cardsCopy.remove(uselessAction)
                uselessAction = getUselessAction(cardsCopy)
            }
        }
        //next add coppers
        if (cardsNotNeeded.size < numCardsNotNeeded) {
            for (card in cardsCopy) {
                if (card.isCopper) {
                    cardsNotNeeded.add(Copper.NAME)
                    if (cardsNotNeeded.size == numCardsNotNeeded) {
                        break
                    }
                } else {
                    remainingCards.add(card)
                }
            }
        }
        //next add lowest cost cards
        if (cardsNotNeeded.size < numCardsNotNeeded) {
            val ccc = CardCostComparator()
            Collections.sort(remainingCards, ccc)
            for (card in remainingCards) {
                cardsNotNeeded.add(card.name)
                if (cardsNotNeeded.size == numCardsNotNeeded) {
                    break
                }
            }
        }
        return cardsNotNeeded
    }

    fun wantsCoppers(): Boolean {
        return isHasCountingHouse || isGardensStrategy || isHasGoons && game.goonsCardsPlayed > 0
    }

    fun handleCardAction(oldCardAction: OldCardAction) {
        if (oldCardAction.isWaitingForPlayers) {
            return
        }
        when(oldCardAction.deck) {
            Deck.Kingdom -> KingdomComputerCardActionHandler.handleCardAction(oldCardAction, this)
            Deck.Intrigue -> IntrigueComputerCardActionHandler.handleCardAction(oldCardAction, this)
            Deck.Seaside -> SeasideComputerCardActionHandler.handleCardAction(oldCardAction, this)
            Deck.Alchemy -> AlchemyComputerCardActionHandler.handleCardAction(oldCardAction, this)
            Deck.Prosperity -> ProsperityComputerCardActionHandler.handleCardAction(oldCardAction, this)
            Deck.Cornucopia -> CornucopiaComputerCardActionHandler.handleCardAction(oldCardAction, this)
            Deck.Hinterlands -> HinterlandsComputerCardActionHandler.handleCardAction(oldCardAction, this)
            Deck.Promo -> PromoComputerCardActionHandler.handleCardAction(oldCardAction, this)
            Deck.Reaction -> ReactionComputerCardActionHandler.handleCardAction(oldCardAction, this)
            else -> throw RuntimeException("OldCardAction with card: " + oldCardAction.cardName + " and type: " + oldCardAction.type + " does not have a deck type")
        }
    }
}
