package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.supply.*
import com.kingdom.model.players.BotPlayer

class BigMoneyBotPlayer(user: User, game: Game) : BotPlayer(user, game) {

    override val difficulty: Int = 3

    override fun getCardToBuy(): String? {

        return when {
            game.isIncludeColonyCards && availableCardsToBuyNames.contains(Colony.NAME) -> Colony.NAME
            game.isIncludePlatinumCards && game.isIncludeColonyCards && turns < 10 && cardCountByName(Platinum.NAME) == 0 && availableCardsToBuyNames.contains(Platinum.NAME) -> Platinum.NAME
            availableCardsToBuyNames.contains(Province.NAME) -> Province.NAME
            game.numInPileMap[Province.NAME]!! <= 5 && availableCardsToBuyNames.contains(Duchy.NAME) -> Duchy.NAME
            game.numInPileMap[Province.NAME]!! <= 2 && availableCardsToBuyNames.contains(Estate.NAME) -> Estate.NAME
            availableCardsToBuyNames.contains(Gold.NAME) -> Gold.NAME
            availableCardsToBuyNames.contains(Silver.NAME) -> Silver.NAME
            else -> null
        }

    }
}