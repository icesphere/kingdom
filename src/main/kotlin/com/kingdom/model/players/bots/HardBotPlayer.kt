package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card

class HardBotPlayer(user: User, game: Game) : MediumBotPlayer(user, game) {

    //todo better buying card strategy

    //todo
    val bigMoneyStrategy = false

    //todo
    val bigActionsStrategy = false

    override fun excludeCard(card: Card): Boolean {

        if (super.excludeCard(card)) {
            return true
        }

        val actionsBought = cardCountByExpression { it.isAction }
        val terminalActionsBought = cardCountByExpression { it.isTerminalAction }

        when {
            //todo game.buyingCardWillEndGame(card.name) && !game.currentlyWinning(userId) -> return true
            card.isVictoryOnly -> return true
            card.name == "Mine" || card.name == "Thief" || card.name == "Chancellor"
                    || card.name == "Wishing Well" || card.name == "Workshop"
                    || card.name == "Horn of Plenty" || card.name == "Quarry"
                    || card.name == "Trader" || card.name == "Navigator"
                    || card.name == "Oracle" || card.name == "Fool's Gold" -> return true
            card.name == "Workshop" && !isGardensStrategy -> return true
            turns < 2 && card.name == "Village" -> return true
            bigMoneyStrategy && card.isTerminalAction && terminalActionsBought - actionsBought > 0 -> return true
            bigMoneyStrategy && card.isAction && !card.isVictory && actionsBought >= 3 -> return true
            !bigActionsStrategy && card.isAction && !card.isVictory && actionsBought >= 5 -> return true
            //card.name == "Expand" && cardsGained["Expand"] != null -> return true
            bigMoneyStrategy && card.isExtraActionsCard -> return true
            !isGardensStrategy && card.name == "Talisman" -> return true
            else -> return false
        }

    }
}