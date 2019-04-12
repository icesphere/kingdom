package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForLandmark
import com.kingdom.model.cards.listeners.AfterCardGainedListener
import com.kingdom.model.players.Player

class CursedMarket : EmpiresLandmark(NAME), GameSetupModifier, AfterCardGainedListener, AfterCardBoughtListenerForLandmark {

    init {
        special = "When you gain an Action, move 1 VP from its pile to this. When you buy a Curse, take the VP from this. Setup: Put 2 VP on each non-Gathering Action Supply pile."
        fontSize = 9
    }

    override fun modifyGameSetup(game: Game) {
        game.kingdomCards.filter { it.isAction && !it.isGathering }
                .forEach { game.addVictoryPointsToSupplyPile(it.pileName, 2) }
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.isAction && player.game.getVictoryPointsOnSupplyPile(card.pileName) > 0) {
            player.moveVictoryPointsOnSupplyPile(card, this, 1)
        }
    }

    override fun afterCardBought(card: Card, player: Player) {
        if (card.isCurse && player.game.getVictoryPointsOnSupplyPile(NAME) > 0) {
            player.takeAllVictoryPointsFromSupplyPile(this)
        }
    }

    companion object {
        const val NAME: String = "Cursed Market"
    }
}