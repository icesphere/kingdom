package com.kingdom.model.cards

import com.kingdom.model.Game

interface GameSetupModifier {

    fun modifyGameSetup(game: Game)
}