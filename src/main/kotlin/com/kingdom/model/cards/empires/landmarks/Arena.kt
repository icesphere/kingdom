package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Arena : EmpiresLandmark(NAME), GameSetupModifier, DiscardCardsForBenefitActionCard {

    init {
        special = "At the start of your Buy phase, you may discard an Action card. If you do, take 2 VP from here. Setup: Put 6 VP here per player."
    }

    override fun modifyGameSetup(game: Game) {
        game.addVictoryPointsToSupplyPile(NAME, 6 * game.numPlayers)
    }

    override fun isLandmarkActionable(player: Player): Boolean {
        return !player.isBuyPhase && player.hand.any { it.isAction } && player.game.getVictoryPointsOnSupplyPile(NAME) > 0
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsForBenefit(this, 1, "Discard an Action card", { c -> c.isAction })
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        player.takeVictoryPointsFromSupplyPile(this, 2)
        player.actionTakeInBuyPhase()
    }

    companion object {
        const val NAME: String = "Arena"
    }
}