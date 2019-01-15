package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.intrigue.WishingWell
import com.kingdom.model.cards.base.Mine
import com.kingdom.model.cards.base.Village
import com.kingdom.model.cards.base.Workshop
import com.kingdom.model.cards.prosperity.Expand
import com.kingdom.model.cards.prosperity.Quarry

class HardBotPlayer(user: User, game: Game) : MediumBotPlayer(user, game) {

    //todo better buying card strategy

    override val difficulty: Int = 3

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
            card.name == Mine.NAME
                    || card.name == WishingWell.NAME || card.name == Workshop.NAME
                    || card.name == "Horn of Plenty" || card.name == Quarry.NAME
                    || card.name == "Trader"
                    || card.name == "Oracle" || card.name == "Fool's Gold" -> return true
            card.name == Workshop.NAME && !isGardensStrategy -> return true
            turns < 2 && card.name == Village.NAME -> return true
            bigMoneyStrategy && card.isTerminalAction && terminalActionsBought - actionsBought > 0 -> return true
            bigMoneyStrategy && card.isAction && !card.isVictory && actionsBought >= 3 -> return true
            !bigActionsStrategy && card.isAction && !card.isVictory && actionsBought >= 5 -> return true
            card.name == Expand.NAME && cardCountByName(card.name) > 0 -> return true
            bigMoneyStrategy && card.isExtraActionsCard -> return true
            else -> return false
        }

    }
}