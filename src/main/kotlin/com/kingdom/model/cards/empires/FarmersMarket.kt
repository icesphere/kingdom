package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class FarmersMarket : EmpiresCard(NAME, CardType.ActionGathering, 3) {

    init {
        addBuys = 1
        special = "If there are 4 VP or more on the Farmers Market Supply pile, take them and trash this. Otherwise, add 1 VP to the pile and then +\$1 per 1 VP on the pile."
        fontSize = 9
        isAddCoinsCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val victoryPointsOnSupplyPile = player.game.victoryPointsOnSupplyPile[pileName] ?: 0
        if (victoryPointsOnSupplyPile >= 4) {
            player.takeAllVictoryPointsFromSupplyPile(this)
            player.trashCardInPlay(this)
            player.game.refreshSupply()
        } else {
            player.game.addVictoryPointToSupplyPile(pileName)
            player.addCoins(victoryPointsOnSupplyPile + 1)
            player.addEventLogWithUsername("added 1 VP to $cardNameWithBackgroundColor Supply pile and gained +\$${victoryPointsOnSupplyPile + 1}")
        }
    }

    companion object {
        const val NAME: String = "Farmers Market"
    }
}

