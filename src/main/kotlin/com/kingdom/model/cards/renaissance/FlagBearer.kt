package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Artifact
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ArtifactAction
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.cards.renaissance.artifacts.Flag
import com.kingdom.model.players.Player

class FlagBearer : RenaissanceCard(NAME, CardType.Action, 4), ArtifactAction, AfterCardGainedListenerForSelf, AfterCardTrashedListenerForSelf {

    init {
        addCoins = 2
        special = "When you gain or trash this, take the Flag."
    }

    override val artifacts: List<Artifact>
        get() = listOf(Flag())

    override fun afterCardGained(player: Player) {
        player.takeFlag()
    }

    override fun afterCardTrashed(player: Player) {
        player.takeFlag()
    }

    companion object {
        const val NAME: String = "Flag Bearer"
    }
}