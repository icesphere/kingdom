package com.kingdom.model.cards.renaissance.projects

class Cathedral : RenaissanceProject(NAME, 3) {

    init {
        special = "At the start of your turn, trash a card from your hand."
    }

    companion object {
        const val NAME: String = "Cathedral"
    }
}