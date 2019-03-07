package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForLandmark
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForLandmark
import com.kingdom.model.players.Player

class Labyrinth : EmpiresLandmark(NAME), GameSetupModifier, AfterCardGainedListenerForLandmark {

    init {
        special = "When you gain a 2nd card in one of your turns, take 2 VP from here. Setup: Put 6 VP here per player."
    }

    override fun modifyGameSetup(game: Game) {
        game.addVictoryPointsToSupplyPile(NAME, 6 * game.numPlayers)
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (player.isYourTurn && player.currentTurnSummary.cardsGained.size == 2 && player.game.getVictoryPointsOnSupplyPile(NAME) > 0) {
            player.takeVictoryPointsFromSupplyPile(this, 2)
        }
    }

    companion object {
        const val NAME: String = "Labyrinth"
    }
}