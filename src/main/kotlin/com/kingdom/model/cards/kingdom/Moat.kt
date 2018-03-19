package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Moat : KingdomCard(NAME, CardType.ActionReaction, 2) {
    init {
        addCards = 2
        special = "When another player plays an Attack card, you may first reveal this from your hand, to be unaffected by it."
        fontSize = 13
        textSize = 81
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Moat"
    }
}

