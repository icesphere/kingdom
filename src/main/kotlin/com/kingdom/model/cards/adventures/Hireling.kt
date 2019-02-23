package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.PermanentDuration
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class Hireling : AdventuresCard(NAME, CardType.ActionDuration, 6), StartOfTurnDurationAction, PermanentDuration {

    init {
        special = "At the start of each of your turns for the rest of the game: +1 Card. (This stays in play.)"
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCard()
        player.showInfoMessage("Gained +1 Card from ${this.cardNameWithBackgroundColor}")
    }

    companion object {
        const val NAME: String = "Hireling"
    }
}

