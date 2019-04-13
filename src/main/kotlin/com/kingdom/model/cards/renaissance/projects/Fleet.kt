package com.kingdom.model.cards.renaissance.projects

class Fleet : RenaissanceProject(NAME, 5) {

    //todo figure out how to do this

    init {
        disabled = true
        special = "After the game ends, there’s an extra round of turns just for players with this."
    }

    companion object {
        const val NAME: String = "Fleet"
    }
}