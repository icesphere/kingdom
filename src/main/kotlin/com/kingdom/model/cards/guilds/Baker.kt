package com.kingdom.model.cards.guilds

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.GameStartedListener

class Baker : GuildsCard(NAME, CardType.Action, 5), GameStartedListener {

    init {
        addCards = 1
        addActions = 1
        addCoffers = 1
        special = "Setup: Each player gets +1 Coffers."
    }

    override fun onGameStarted(game: Game) {
        game.players.forEach { it.addCoffers(1) }
    }

    companion object {
        const val NAME: String = "Baker"
    }
}

