package com.kingdom.model.cards.prosperity

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier

class Monument : ProsperityCard(NAME, CardType.Action, 4), GameSetupModifier {

    init {
        addCoins = 2
        addVictoryCoins = 1
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowVictoryCoins = true
    }

    companion object {
        const val NAME: String = "Monument"
    }
}

