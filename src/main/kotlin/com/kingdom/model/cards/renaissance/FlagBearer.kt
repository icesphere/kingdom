package com.kingdom.model.cards.renaissance

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.cards.renaissance.artifacts.Flag
import com.kingdom.model.players.Player

class FlagBearer : RenaissanceCard(NAME, CardType.Action, 4), GameSetupModifier, AfterCardGainedListenerForSelf, AfterCardTrashedListenerForSelf {

    init {
        disabled = true
        addCoins = 2
        special = "When you gain or trash this, take the Flag."
    }

    override fun modifyGameSetup(game: Game) {
        game.artifacts.add(Flag())
    }

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