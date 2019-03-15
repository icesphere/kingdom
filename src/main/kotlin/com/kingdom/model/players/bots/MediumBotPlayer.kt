package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.GameError
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Event
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.cards.adventures.Amulet
import com.kingdom.model.cards.adventures.Gear
import com.kingdom.model.cards.adventures.Raze
import com.kingdom.model.cards.adventures.Storyteller
import com.kingdom.model.cards.adventures.events.Alms
import com.kingdom.model.cards.adventures.events.Borrow
import com.kingdom.model.cards.adventures.events.Trade
import com.kingdom.model.cards.guilds.Doctor
import com.kingdom.model.cards.guilds.Masterpiece
import com.kingdom.model.cards.guilds.Stonemason
import com.kingdom.model.cards.hinterlands.Farmland
import com.kingdom.model.cards.base.ThroneRoom
import com.kingdom.model.cards.base.Witch
import com.kingdom.model.cards.cornucopia.Remake
import com.kingdom.model.cards.darkages.*
import com.kingdom.model.cards.empires.events.*
import com.kingdom.model.cards.intrigue.Upgrade
import com.kingdom.model.cards.prosperity.Contraband
import com.kingdom.model.cards.prosperity.Forge
import com.kingdom.model.cards.prosperity.KingsCourt
import com.kingdom.model.cards.prosperity.Mint
import com.kingdom.model.cards.seaside.Lookout
import com.kingdom.model.cards.seaside.TreasureMap
import com.kingdom.model.cards.supply.*

open class MediumBotPlayer(user: User, game: Game) : EasyBotPlayer(user, game) {

    //todo better buying card strategy

    override val difficulty: Int = 2

    override val onlyBuyVictoryCards: Boolean
        get() {
            var shouldOnlyBuyVictoryCards = false
            val provincesInSupply = game.numInPileMap[Province.NAME]
            if (provincesInSupply == null) {
                val error = GameError(GameError.COMPUTER_ERROR, "Supply was null for Province")
                game.logError(error)
            }
            if (game.numPlayers == 2 && game.numInPileMap[Province.NAME]!! <= 3 || game.numPlayers > 2 && game.numInPileMap[Province.NAME]!! <= 4) {
                shouldOnlyBuyVictoryCards = true
            } else if (game.isIncludeColonyCards && (game.numPlayers == 2 && game.numInPileMap[Colony.NAME]!! <= 2 || game.numPlayers > 2 && game.numInPileMap[Colony.NAME]!! <= 3)) {
                shouldOnlyBuyVictoryCards = true
            } else if (difficulty >= 2) {
                var pilesWithOneCard = 0
                var pilesWithTwoCards = 0
                for (numInSupply in game.numInPileMap.values) {
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
                    shouldOnlyBuyVictoryCards = true
                }
            }
            return shouldOnlyBuyVictoryCards
        }

    override fun getBuyEventScore(event: Event): Int {
        return when (event.name) {
            Advance.NAME -> if (availableCoins < 5 && hand.any { it.isAction && it.cost <= 3 }) 5 else 0
            Alms.NAME -> if (availableCoins < 4) 5 else 0
            Borrow.NAME -> {
                return when (availableCoins) {
                    10 -> if (cardCountByName(Platinum.NAME) > 0 && game.isIncludeColonyCards && game.isCardAvailableInSupply(Colony())) 30 else 0
                    8 -> if (cardCountByName(Platinum.NAME) == 0 && game.isIncludePlatinumCards && game.isCardAvailableInSupply(Platinum())) 20 else 0
                    7 -> if (cardCountByName(Gold.NAME) > 0 && game.isCardAvailableInSupply(Province())) 15 else 0
                    5 -> if (cardCountByName(Gold.NAME) == 0 && game.isCardAvailableInSupply(Gold())) 5 else 0
                    else -> 0
                }
            }
            Dominate.NAME -> 100
            Donate.NAME -> if (cardCountByName(Curse.NAME) >= 3) 7 else 0
            Trade.NAME -> hand.count { getTrashCardScore(it) > 100 } * 5
            Triumph.NAME -> if (currentTurnSummary.cardsGained.size >= 3) 5 else 0
            Wedding.NAME -> if (cardCountByName(Gold.NAME) == 0 || availableCoins == 7) 3 else 0
            Windfall.NAME -> 10
            else -> super.getBuyEventScore(event)
        }
    }

    override fun getCardToBuy(): String? {
        if (onlyBuyVictoryCards) {
            return availableCardsToBuy.filter { it.isVictory }.maxBy {
                if (it is VictoryPointsCalculator) {
                    it.calculatePoints(this)
                } else {
                    it.victoryPoints
                }
            }?.name
        }

        return super.getCardToBuy()
    }

    override fun excludeCard(card: Card): Boolean {

        if (game.isShowEmbargoTokens) {
            val embargoTokens = game.embargoTokens[card.name] ?: 0
            if (embargoTokens > 0) {
                if (embargoTokens > 2 || !card.isProvince && !card.isColony) {
                    return true
                }
            }
        }

        //todo trashing strategy

        val terminalActionsBought = cardCountByExpression { it.isTerminalAction }
        val actionsBought = cardCountByExpression { it.isAction }

        //todo better logic here
        val includeVictoryOnlyCards = (!game.isIncludeColonyCards && turns > 8) || turns > 12

        return when {
            !card.isVictory && !card.isTreasure && getCardCostWithModifiers(card) < 5 && cardCountByName(card.name) >= 3 -> true
            card.name == ThroneRoom.NAME && (turns < 3 || cardCountByName(card.name) >= 2) -> true
            card.name == KingsCourt.NAME && cardCountByName(card.name) >= 2 -> return true
            card.isTerminalAction && (terminalActionsBought - actionsBought > 1 || terminalActionsBought == 1 && actionsBought == 1) -> true
            card.name == Lookout.NAME -> return true
            card.isVictoryOnly && !includeVictoryOnlyCards -> true
            card.name == Forge.NAME -> return true
            card.name == Rats.NAME -> return true
            card.name == Witch.NAME && turns >= 8 -> true
            card.name == Mint.NAME && turns >= 5 -> return true
            card.name == TreasureMap.NAME -> return true
            card.name == Copper.NAME -> return true
            card.name == Doctor.NAME -> return true
            card.name == Masterpiece.NAME -> return true
            card.name == Stonemason.NAME -> return true
            card.name == Raze.NAME -> return true
            card.name == Amulet.NAME -> return true
            card.name == Gear.NAME -> return true
            card.name == Storyteller.NAME -> return true
            card.name == Armory.NAME -> return true
            card.name == BandOfMisfits.NAME -> return true
            card.name == Procession.NAME -> return true
            card.name == Feodum.NAME && cardCountByName(Silver.NAME) < 6 -> return true
            card.name == Farmland.NAME && hand.all { it.isVictory && it.cost > 2 } -> return true
            card is TavernCard -> return true
            card.isRuins -> return true
            card.debtCost > 0 -> return true
            else -> super.excludeCard(card)
        }

    }

    override fun getPlayCardScore(card: Card): Int {
        return when {
            card.isTrashingFromHandRequiredCard -> {
                val cardToTrash = getCardToTrashFromHand(false, { c -> c.id == card.id })
                return when {
                    cardToTrash == null -> -1
                    !card.isTrashingFromHandToUpgradeCard && getBuyCardScore(cardToTrash) > 3 -> -1
                    card.isTrashingFromHandToUpgradeCard && (cardToTrash.isProvince || cardToTrash.isColony) -> -1
                    (card.name == Upgrade.NAME || card.name == Remake.NAME) && cardToTrash.cost > 2 && game.availableCards.none { it.cost == card.cost + 1 } -> 1
                    card.addActions > 0 -> 10
                    else -> card.cost
                }
            }
            card.addActions > 0 -> 10
            card is Contraband && availableCoins >= 11 || (availableCoins >= 8 && !game.isIncludeColonyCards) -> -1
            else -> card.cost
        }
    }

}