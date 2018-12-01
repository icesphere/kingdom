package com.kingdom.model.cards.listeners

import com.kingdom.model.Game

interface GameStartedListener {

    fun onGameStarted(game: Game)
}