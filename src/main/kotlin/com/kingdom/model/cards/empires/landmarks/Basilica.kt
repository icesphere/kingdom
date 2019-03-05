package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForLandmark
import com.kingdom.model.players.Player

class Basilica : EmpiresLandmark(NAME), GameSetupModifier, AfterCardBoughtListenerForLandmark {

    init {
        special = "When you buy a card, if you have \$2 or more left, take 2 VP from here. Setup: Put 6 VP here per player."
    }

    override fun modifyGameSetup(game: Game) {
        game.addVictoryPointsToSupplyPile(NAME, 6 * game.numPlayers)
    }

    override fun afterCardBought(card: Card, player: Player) {
        if (player.availableCoins >= 2 && player.game.getVictoryPointsOnSupplyPile(NAME) > 0) {
            player.takeVictoryPointsFromSupplyPile(this, 2)
        }
    }

    companion object {
        const val NAME: String = "Basilica"
    }
}