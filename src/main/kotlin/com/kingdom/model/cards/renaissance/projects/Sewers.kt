package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.listeners.AfterCardTrashedListener
import com.kingdom.model.players.Player

class Sewers : RenaissanceProject(NAME, 3), AfterCardTrashedListener {

    //todo figure out better way to do this, so that it can let you trash a card per card trashed (e.g. when Chapel trashes more than one card)
    var ignoreNextCardTrashed = false

    init {
        special = "When you trash a card other than with this, you may trash a card from your hand."
    }

    override fun afterCardTrashed(card: Card, player: Player) {

        if (ignoreNextCardTrashed) {
            ignoreNextCardTrashed = false
            return
        }

        ignoreNextCardTrashed = true

        player.optionallyTrashCardsFromHand(1, "You may trash a card from your hand")
    }

    companion object {
        const val NAME: String = "Sewers"
    }
}