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

        val highestCostCard = availableCardsToBuy.maxBy { it.cost }

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