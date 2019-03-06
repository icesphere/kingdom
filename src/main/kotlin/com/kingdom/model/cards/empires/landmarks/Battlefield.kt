package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForLandmark
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForLandmark
import com.kingdom.model.players.Player

class Battlefield : EmpiresLandmark(NAME), GameSetupModifier, AfterCardGainedListenerForLandmark {

    init {
        special = "When you gain a Victory card, take 2 VP from here. Setup: Put 6 VP here per player."
    }

    override fun modifyGameSetup(game: Game) {
        game.addVictoryPointsToSupplyPile(NAME, 6 * game.numPlayers)
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.isVictory && player.game.getVictoryPointsOnSupplyPile(NAME) > 0) {
            player.takeVictoryPointsFromSupplyPile(this, 2)
        }
    }

    companion object {
        const val NAME: String = "Battlefield"
    }
}