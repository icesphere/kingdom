package com.kingdom.model.cards.renaissance.projects

class Capitalism : RenaissanceProject(NAME, 5) {

    //todo figure out how to do this

    init {
        disabled = true
        special = "During your turns, Actions with +\$ amounts in their text are also Treasures."
    }

    companion object {
        const val NAME: String = "Capitalism"
    }
}