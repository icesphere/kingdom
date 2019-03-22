package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.supply.*

class BigMoneyBotPlayer(user: User, game: Game) : HardBotPlayer(user, game) {

    override val difficulty: Int = 3

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

        return when {
            game.isIncludeColonyCards && availableCardsToBuyNames.contains(Colony.NAME) -> Colony.NAME
            game.isIncludePlatinumCards && availableCardsToBuyNames.contains(Platinum.NAME) -> Platinum.NAME
            availableCardsToBuyNames.contains(Province.NAME) -> Province.NAME
            availableCardsToBuyNames.contains(Gold.NAME) -> Gold.NAME
            availableCardsToBuyNames.contains(Silver.NAME) -> Silver.NAME
            else -> null
        }

    }
}