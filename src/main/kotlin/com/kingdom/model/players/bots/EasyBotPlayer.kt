package com.kingdom.model.players.bots

import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.base.Chapel
import com.kingdom.model.cards.base.Gardens
import com.kingdom.model.cards.base.Laboratory
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Platinum
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.BotPlayer

open class EasyBotPlayer(user: User, game: Game) : BotPlayer(user, game) {

    //todo
    val isGardensStrategy = false

    //todo
    val isHasGoons = false

    //todo
    val chapelStrategy = false

    //todo
    val laboratoryStrategy = false

    //todo check !isTrackContrabandCards || !game.contrabandCards.contains(getKingdomCard("Chapel")

    override val difficulty: Int = 1

    override fun getCardToBuy(): String? {

        val highestCostCard = availableCardsToBuy.maxBy { getBuyCardScore(it) }

        if (highestCostCard != null) {
            val cards = availableCardsToBuy.filter { it.cost == highestCostCard.cost }
            return cards.shuffled().first().name
        }

        return null
    }

    override fun excludeCard(card: Card): Boolean {
        if (card.name == Chapel.NAME) {
            return true
        }

        return super.excludeCard(card)
    }

}