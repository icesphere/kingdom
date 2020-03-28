package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.BeforeBuyPhaseListenerForLandmarks
import com.kingdom.model.players.Player

class Arena : EmpiresLandmark(NAME), GameSetupModifier, DiscardCardsForBenefitActionCard, BeforeBuyPhaseListenerForLandmarks {

    init {
        special = "At the start of your Buy phase, you may discard an Action card. If you do, take 2 VP from here. Setup: Put 6 VP here per player."
    }

    override fun modifyGameSetup(game: Game) {
        game.addVictoryPointsToSupplyPile(NAME, 6 * game.numPlayers)
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        if (discardedCards.isNotEmpty()) {
            player.takeVictoryPointsFromSupplyPile(this, 2)
            player.actionTakenInBuyPhase()
        }
    }

    override fun beforeBuyPhase(player: Player) {
        if (!player.isBuyPhase && player.hand.any { it.isAction } && player.game.getVictoryPointsOnSupplyPile(NAME) > 0) {
            player.optionallyDiscardCardsForBenefit(this, 1, "Discard an Action card to take 2 VP from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Arena"
    }
}