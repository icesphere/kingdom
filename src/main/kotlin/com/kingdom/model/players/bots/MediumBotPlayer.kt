package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.GameError
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.cards.adventures.Amulet
import com.kingdom.model.cards.adventures.Gear
import com.kingdom.model.cards.adventures.Raze
import com.kingdom.model.cards.adventures.Storyteller
import com.kingdom.model.cards.darkages.Rats
import com.kingdom.model.cards.guilds.Doctor
import com.kingdom.model.cards.guilds.Masterpiece
import com.kingdom.model.cards.guilds.Stonemason
import com.kingdom.model.cards.hinterlands.Farmland
import com.kingdom.model.cards.kingdom.ThroneRoom
import com.kingdom.model.cards.kingdom.Witch
import com.kingdom.model.cards.prosperity.Forge
import com.kingdom.model.cards.prosperity.KingsCourt
import com.kingdom.model.cards.prosperity.Mint
import com.kingdom.model.cards.seaside.Lookout
import com.kingdom.model.cards.seaside.TreasureMap
import com.kingdom.model.cards.supply.Colony
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Province
import com.kingdom.model.cards.supply.VictoryPointsCalculator

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
            if (game.numPlayers == 2 && game.numInPileMap[Province.NAME]!! <= 2 || game.numPlayers > 2 && game.numInPileMap[Province.NAME]!! <= 3) {
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
                if (game.emptyPiles + pilesWithOneCard + pilesWithTwoCards == numEmptyPilesForGameEnd) {
                    shouldOnlyBuyVictoryCards = true
                }
            }
            return shouldOnlyBuyVictoryCards
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
            card.name == Farmland.NAME && hand.all { it.isVictory && it.cost > 2 } -> return true
            card is TavernCard -> return true
            card.isRuins -> return true
            else -> super.excludeCard(card)
        }

    }

}