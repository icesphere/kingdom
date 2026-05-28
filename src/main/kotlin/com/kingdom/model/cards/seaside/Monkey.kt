package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.listeners.AfterOtherPlayerCardGainedListenerForCardsInPlay
import com.kingdom.model.players.Player

class Monkey : SeasideCard(NAME, CardType.ActionDuration, 3), StartOfTurnDurationAction,
        AfterOtherPlayerCardGainedListenerForCardsInPlay {

    private var isWatchingGains = false

    init {
        addCards = 1
        special = "At the start of your next turn, +1 Card. Until then, when another player gains a card, +1 Card."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        isWatchingGains = true
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCard()
        isWatchingGains = false
        player.showInfoMessage("Gained +1 Card from ${cardNameWithBackgroundColor}")
    }

    override fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        if (isWatchingGains) {
            player.drawCard()
            player.showInfoMessage("${otherPlayer.username} gained ${card.cardNameWithBackgroundColor}; $cardNameWithBackgroundColor drew you a card")
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        isWatchingGains = false
    }

    companion object {
        const val NAME: String = "Monkey"
    }
}
