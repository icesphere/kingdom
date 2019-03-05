package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Game
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.listeners.TurnEndedListenerForLandmark
import com.kingdom.model.players.Player

class Baths : EmpiresLandmark(NAME), GameSetupModifier, TurnEndedListenerForLandmark {

    init {
        special = "When you end your turn without having gained a card, take 2 VP from here."
    }

    override fun modifyGameSetup(game: Game) {
        game.addVictoryPointsToSupplyPile(NAME, 6 * game.numPlayers)
    }

    override fun onTurnEnded(player: Player) {
        if (player.currentTurnSummary.cardsGained.isEmpty() && player.game.getVictoryPointsOnSupplyPile(NAME) > 0) {
            player.takeVictoryPointsFromSupplyPile(this, 2)
        }
    }

    companion object {
        const val NAME: String = "Baths"
    }
}