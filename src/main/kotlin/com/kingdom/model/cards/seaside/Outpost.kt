package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.ConditionalDuration
import com.kingdom.model.players.Player

class Outpost : SeasideCard(NAME, CardType.ActionDuration, 5), ConditionalDuration {

    private var triggeredExtraTurn = false

    override val isKeepAtEndOfTurn: Boolean
        get() = triggeredExtraTurn

    init {
        special = "You only draw 3 cards (instead of 5) in this turn’s Clean-up. Take an extra turn after this one. This can’t cause you to take more than two consecutive turns."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.game.previousPlayerId != player.userId && !player.game.isExtraTurnForCurrentPlayer) {
            triggeredExtraTurn = true
            player.numCardsToDrawAtEndOfTurn = 3
            player.game.isExtraTurnForCurrentPlayer = true
            player.addEventLogWithUsername("will take an extra turn from ${cardNameWithBackgroundColor}")
        } else {
            triggeredExtraTurn = false
            player.showInfoMessage("$cardNameWithBackgroundColor cannot cause more than two consecutive turns")
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        triggeredExtraTurn = false
    }

    companion object {
        const val NAME: String = "Outpost"
    }
}
