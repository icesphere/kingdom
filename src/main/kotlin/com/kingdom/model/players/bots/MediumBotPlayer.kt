package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.GameError
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Event
import com.kingdom.model.cards.Project
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.cards.adventures.Amulet
import com.kingdom.model.cards.adventures.Gear
import com.kingdom.model.cards.adventures.Raze
import com.kingdom.model.cards.adventures.Storyteller
import com.kingdom.model.cards.adventures.events.Alms
import com.kingdom.model.cards.adventures.events.Borrow
import com.kingdom.model.cards.adventures.events.Trade
import com.kingdom.model.cards.base.*
import com.kingdom.model.cards.cornucopia.Remake
import com.kingdom.model.cards.darkages.*
import com.kingdom.model.cards.empires.Temple
import com.kingdom.model.cards.empires.events.*
import com.kingdom.model.cards.empires.landmarks.*
import com.kingdom.model.cards.guilds.Doctor
import com.kingdom.model.cards.guilds.Masterpiece
import com.kingdom.model.cards.guilds.Stonemason
import com.kingdom.model.cards.hinterlands.Farmland
import com.kingdom.model.cards.intrigue.Upgrade
import com.kingdom.model.cards.menagerie.Goatherd
import com.kingdom.model.cards.menagerie.Mastermind
import com.kingdom.model.cards.menagerie.UsesExileMat
import com.kingdom.model.cards.prosperity.Contraband
import com.kingdom.model.cards.prosperity.Forge
import com.kingdom.model.cards.prosperity.KingsCourt
import com.kingdom.model.cards.prosperity.Mint
import com.kingdom.model.cards.renaissance.ActingTroupe
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
            when {
                game.numPlayers == 2 && game.numInPileMap[Province.NAME]!! <= 3 || game.numPlayers > 2 && game.numInPileMap[Province.NAME]!! <= 4 -> shouldOnlyBuyVictoryCards = true
                game.isIncludeColonyCards && (game.numPlayers == 2 && game.numInPileMap[Colony.NAME]!! <= 2 || game.numPlayers > 2 && game.numInPileMap[Colony.NAME]!! <= 3) -> shouldOnlyBuyVictoryCards = true
                game.landmarks.any { it.name == Aqueduct.NAME } && game.getVictoryPointsOnSupplyPile(Aqueduct.NAME) >= 4 -> shouldOnlyBuyVictoryCards = true
                difficulty >= 2 -> {
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

    override fun getBuyProjectScore(project: Project): Int {
        //todo

        return super.getBuyProjectScore(project)
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

        val silversBought = cardCountByName(Silver.NAME)
        val goldsBought = cardCountByName(Gold.NAME)

        when {
            availableCoins < 2 && !isGardensStrategy && !isHasGoons -> return null
            chapelStrategy && cardCountByName(Chapel.NAME) == 0 && (availableCoins == 3 && silversBought > 0 || availableCoins == 2) -> return Chapel.NAME
            chapelStrategy && availableCoins <= 3 && silversBought < 2 && goldsBought == 0 && availableCardsToBuyNames.contains(Silver.NAME) -> return Silver.NAME
            chapelStrategy && laboratoryStrategy && availableCoins <= 5 && availableCardsToBuyNames.contains(Laboratory.NAME) -> return Laboratory.NAME
            availableCardsToBuyNames.contains(Gold.NAME) && goldsBought == 0 && (!game.isIncludePlatinumCards || !availableCardsToBuyNames.contains(Platinum.NAME)) -> return Gold.NAME
            isGardensStrategy && turns > 4 && availableCardsToBuyNames.contains(Gardens.NAME) -> return Gardens.NAME
            /*dukeStrategy -> {
                val duchiesInSupply = game.pileAmounts[Duchy.NAME]!!
                if (duchiesInSupply > 0 && (turns > 8 || duchiesInSupply <= 6) && availableCardsToBuyNames.contains(Duke.NAME)) {
                    if (duchiesBought < 4 || duchiesBought <= dukesBought && (!game.isTrackContrabandCards || !game.contrabandCards.contains(game.duchyCard))) {
                        return game.duchyCard
                    } else if (duchiesBought >= 3 && game.canBuyCard(player, getKingdomCard("Duke"))) {
                        return getKingdomCard("Duke")
                    }
                }
            }*/
            /*checkPeddler && !onlyBuyVictoryCards() && game.canBuyCard(player, getKingdomCard("Peddler")) -> if (coins < 6) {
                return getKingdomCard("Peddler")
            }*/
            //cityStrategy && !onlyBuyVictoryCards() && coins <= 6 && turns > 8 && game.canBuyCard(player, getKingdomCard("City")) -> return getKingdomCard("City")
            //ambassadorStrategy && !onlyBuyVictoryCards() && turns < 2 -> return getKingdomCard("Ambassador")
            //pirateShipStrategy && !onlyBuyVictoryCards() && coins <= 4 && terminalActionsBought - actionsBought < 2 && game.canBuyCard(player, getKingdomCard("Pirate Ship")) -> return getKingdomCard("Pirate Ship")
        }

        return super.getCardToBuy()
    }

    override fun getBuyCardScore(card: Card): Int {
        //todo better logic

        val cost = getCardCostWithModifiers(card)

        if (card.isCurseOnly) {
            return -1
        }

        if (excludeCard(card)) {
            return 0
        }

        var costModification = 0

        if (card is Temple) {
            val victoryPointsOnPile = game.getVictoryPointsOnSupplyPile(card.pileName)
            if (victoryPointsOnPile > 0) {
                costModification += victoryPointsOnPile - 1
            }
        }

        if (game.landmarks.any { it is BanditFort } && (card.isSilver || card.isGold)) {
            costModification -= 2
        }

        if (game.landmarks.any { it is Battlefield } && card.isVictory) {
            costModification += 1
        }

        if (game.landmarks.any { it is Museum } && allCards.none { it.name == card.name }) {
            costModification += 1
        }

        if (game.landmarks.any { it is Obelisk } && card.name == (game.landmarks.first { it is Obelisk } as Obelisk).chosenPile) {
            costModification += 2
        }

        if (game.landmarks.any { it is WolfDen }) {
            val cardCount = cardCountByName(card.name)
            if (cardCount == 1) {
                costModification += 2
            } else if (cardCount == 0) {
                costModification -= 1
            }
        }

        if (game.landmarks.any { it is Colonnade } && inPlay.any { it.name == card.name }) {
            costModification += 1
        }

        if (card.isCopper && cardCountByName(card.name) < 15 && game.landmarks.any { it is Fountain } && turns > 5) {
            costModification += 1
            if (buys > 1) {
                costModification += 1
            }
        }

        if (costModification != 0) {
            return cost + costModification
        }

        return super.getBuyCardScore(card)
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

        //todo exile cards

        //todo trashing strategy
        val trashingCards = cardCountByExpression { it.isTrashingCard }
        if (card.isTrashingCard && (onlyBuyVictoryCards || turns > 7 || trashingCards >= 2 || (turns > 4 && trashingCards > 0))) {
            return true
        }

        val terminalActionsBought = cardCountByExpression { it.isTerminalAction }
        val actionsBought = cardCountByExpression { it.isAction }

        //todo better logic here
        val includeVictoryOnlyCards = onlyBuyVictoryCards || cardCountByName(Platinum.NAME) > 0 || cardCountByName(Gold.NAME) > 1 || cardCountByExpression { it.cost >= 5 } > 3

        return when {
            !card.isVictory && !card.isTreasure && getCardCostWithModifiers(card) < 5 && cardCountByName(card.name) >= 3 -> true
            card.name == ThroneRoom.NAME && (actionsBought < 3 || cardCountByName(card.name) >= 2) -> true
            card.name == KingsCourt.NAME && (actionsBought < 3 || cardCountByName(card.name) >= 2) -> true
            card.name == Mastermind.NAME && (actionsBought < 3 || cardCountByName(card.name) >= 2) -> true
            card.isTerminalAction && (terminalActionsBought - actionsBought > 1 || terminalActionsBought == 1 && actionsBought == 1) -> true
            card.name == Lookout.NAME -> true
            card.isVictoryOnly && !includeVictoryOnlyCards -> true
            card.name == Forge.NAME -> true
            card.name == Rats.NAME -> true
            card.name == Witch.NAME && turns >= 8 -> true
            card.name == Mint.NAME && turns >= 5 -> true
            card.name == TreasureMap.NAME -> true
            card.name == Copper.NAME -> !(cardCountByName(card.name) < 15 && game.landmarks.any { it is Fountain } && turns > 5)
            card.name == Doctor.NAME -> true
            card.name == Masterpiece.NAME -> true
            card.name == Stonemason.NAME -> true
            card.name == Raze.NAME -> true
            card.name == Goatherd.NAME -> true
            card.name == Amulet.NAME -> true
            card.name == Gear.NAME -> true
            card.name == Storyteller.NAME -> true
            card.name == Armory.NAME -> true
            card.name == BandOfMisfits.NAME -> true
            card.name == Procession.NAME -> true
            card.name == Feodum.NAME && cardCountByName(Silver.NAME) < 6 -> true
            card.name == Farmland.NAME && hand.all { it.isVictory && it.cost > 2 } -> true
            card.name == ActingTroupe.NAME && terminalActionsBought > 0 && villagers == 0 -> true
            card is TavernCard -> true
            card.isRuins -> true
            card is UsesExileMat -> true
            card.debtCost > 0 -> true
            game.landmarks.any { it is Wall } && (card.cost < 3 || (card.cost < 5 && allCards.size >= 15)) -> true
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
                    cardToTrash.isProvince || cardToTrash.isColony -> -1
                    (card.name == Upgrade.NAME || card.name == Remake.NAME) && cardToTrash.cost > 2 && game.availableCards.none { it.cost == card.cost + 1 } -> 1
                    card.addActions > 0 -> 10
                    else -> card.cost
                }
            }
            card.addActions > 0 -> 10
            card is Contraband && availableCoins >= 11 || (availableCoins >= 8 && !game.isIncludeColonyCards) -> -1
            card is Counterfeit && hand.any { it !is Counterfeit && it.isTreasure && (buys > 0 || it.cost < 6) } -> 10
            else -> card.cost
        }
    }

}