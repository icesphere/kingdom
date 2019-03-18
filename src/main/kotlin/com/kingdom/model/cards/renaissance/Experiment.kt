package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Experiment : RenaissanceCard(NAME, CardType.Action, 3), AfterCardGainedListenerForSelf {

    var gainedExperiment: Boolean = false

    init {
        addCards = 2
        addActions = 1
        special = "Return this to the supply. When you gain this, gain another Experiment (that doesn't come with another)."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.removeCardInPlay(this, CardLocation.Supply)
        player.game.returnCardToSupply(this)
    }

    override fun afterCardGained(player: Player) {
        var lastExperimentGained = player.currentTurnSummary.cardsGained.lastOrNull { it != this && it is Experiment } as Experiment?
        if (lastExperimentGained == null) {
            lastExperimentGained = this
        }
        if (!lastExperimentGained.gainedExperiment) {
            gainedExperiment = true
            player.gainSupplyCard(Experiment(), true)
        }
    }

    companion object {
        const val NAME: String = "Experiment"
    }
}