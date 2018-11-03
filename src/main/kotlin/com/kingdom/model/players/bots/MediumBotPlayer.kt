package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.kingdom.ThroneRoom
import com.kingdom.model.cards.kingdom.Witch
import com.kingdom.model.cards.prosperity.Forge
import com.kingdom.model.cards.prosperity.KingsCourt
import com.kingdom.model.cards.prosperity.Mint
import com.kingdom.model.cards.seaside.Lookout
import com.kingdom.model.cards.seaside.TreasureMap
import com.kingdom.model.cards.supply.Copper

open class MediumBotPlayer(user: User, game: Game) : EasyBotPlayer(user, game) {

    //todo better buying card strategy

    override fun excludeCard(card: Card): Boolean {

        if (game.isShowEmbargoTokens ) {
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
        val includeVictoryOnlyCards = turns > 8

        return when {
            !card.isVictory && !card.isTreasure && getCardCostWithModifiers(card) < 5 && cardCountByName(card.name) >= 3 -> true
            card.name == ThroneRoom.NAME && (turns < 3 || cardCountByName(card.name) >= 2) -> true
            card.name == KingsCourt.NAME && cardCountByName(card.name) >= 2 -> return true
            card.isTerminalAction && (terminalActionsBought - actionsBought > 1 || terminalActionsBought == 1 && actionsBought == 1) -> true
            card.name == Lookout.NAME -> return true
            card.isVictoryOnly && !includeVictoryOnlyCards -> true
            card.name == Forge.NAME -> return true
            card.name == Witch.NAME && turns >= 8 -> true
            card.name == Mint.NAME && turns >= 5 -> return true
            card.name == TreasureMap.NAME -> return true
            card.name == Copper.NAME -> return true
            //card.name == "Remake" -> return true
            //card.name == "Farmland" && hand.all { it.isProvince } -> return true
            else -> super.excludeCard(card)
        }

    }

}