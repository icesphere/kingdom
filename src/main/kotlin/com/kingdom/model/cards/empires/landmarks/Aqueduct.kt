package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.listeners.CardGainedListenerForLandmark
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Aqueduct : EmpiresLandmark(NAME), GameSetupModifier, CardGainedListenerForLandmark {

    init {
        special = "When you gain a Treasure, move 1 VP from its pile to this. When you gain a Victory card, take the VP from this. Setup: Put 8 VP on the Silver and Gold piles."
    }

    override fun modifyGameSetup(game: Game) {
        game.addVictoryPointsToSupplyPile(Silver.NAME, 8)
        game.addVictoryPointsToSupplyPile(Gold.NAME, 8)
    }

    override fun onCardGained(card: Card, player: Player) {
        if (card.isTreasure && player.game.getVictoryPointsOnSupplyPile(card.pileName) > 0) {
            player.game.addVictoryPointsToSupplyPile(card.pileName, -1)
            player.game.addVictoryPointsToSupplyPile(this.pileName, 1)
            player.addEventLogWithUsername("moved 1 VP from ${card.cardNameWithBackgroundColor} to $cardNameWithBackgroundColor")
        }

        val victoryPointsOnAqueduct = player.game.getVictoryPointsOnSupplyPile(this.pileName)
        if (card.isVictory && victoryPointsOnAqueduct > 0) {
            player.addVictoryCoins(victoryPointsOnAqueduct, true)
            player.game.clearVictoryPointsFromSupplyPile(this.pileName)
        }
    }

    companion object {
        const val NAME: String = "Aqueduct"
    }
}