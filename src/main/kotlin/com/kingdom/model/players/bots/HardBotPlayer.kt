package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.base.Mine
import com.kingdom.model.cards.base.Village
import com.kingdom.model.cards.base.Workshop
import com.kingdom.model.cards.cornucopia.HornOfPlenty
import com.kingdom.model.cards.hinterlands.Trader
import com.kingdom.model.cards.intrigue.WishingWell
import com.kingdom.model.cards.prosperity.Expand
import com.kingdom.model.cards.prosperity.Quarry
import com.kingdom.model.cards.supply.Curse

open class HardBotPlayer(user: User, game: Game) : MediumBotPlayer(user, game) {

    //todo better buying card strategy

    override val difficulty: Int = 3

    //todo
    val bigMoneyStrategy = false

    //todo
    val bigActionsStrategy = false

    override fun getBuyCardScore(card: Card): Int {
        //todo better logic

        val cost = getCardCostWithModifiers(card)

        if (card.isCurseGiver) {
            if (game.numInPileMap.getValue(Curse.NAME) <= 5) {
                return cost - 2
            } else if (turns < 4) {
                return cost + 1
            }
        }

        if (card.addActions > 1 && turns < 2) {
            return cost - 1
        }

        if (cardCountByExpression { it.isAction && it.addActions > 1 } >= cardCountByExpression { it.isAction && it.addActions <= 1 }) {
            return cost - 1
        }

        return super.getBuyCardScore(card)
    }

    override fun excludeCard(card: Card): Boolean {

        if (super.excludeCard(card)) {
            return true
        }

        val actionsBought = cardCountByExpression { it.isAction }
        val terminalActionsBought = cardCountByExpression { it.isTerminalAction }

        return when {
            //todo game.buyingCardWillEndGame(card.name) && !game.currentlyWinning(userId) -> return true
            card.name == Mine.NAME
                    || card.name == WishingWell.NAME || card.name == Workshop.NAME
                    || card.name == HornOfPlenty.NAME || card.name == Quarry.NAME
                    || card.name == Trader.NAME -> true
            card.name == Workshop.NAME && !isGardensStrategy -> true
            turns < 2 && card.name == Village.NAME -> true
            bigMoneyStrategy && card.isTerminalAction && terminalActionsBought - actionsBought > 0 -> true
            bigMoneyStrategy && card.isAction && !card.isVictory && actionsBought >= 3 -> true
            !bigActionsStrategy && card.isAction && !card.isVictory && actionsBought >= 5 -> true
            card.name == Expand.NAME && cardCountByName(card.name) > 0 -> true
            bigMoneyStrategy && card.isExtraActionsCard -> true
            else -> false
        }

    }
}