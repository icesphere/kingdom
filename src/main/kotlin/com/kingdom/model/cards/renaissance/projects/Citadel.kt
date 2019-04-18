package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.listeners.CardPlayedListener
import com.kingdom.model.players.Player

class Citadel : RenaissanceProject(NAME, 8), CardPlayedListener {

    init {
        special = "The first time you play an Action card during each of your turns, play it again afterwards."
    }

    override fun onCardPlayed(card: Card, player: Player) {
        if (card.isAction && player.cardsPlayed.count { it.isAction } == 1) {
            player.addEventLog("$cardNameWithBackgroundColor is playing ${card.cardNameWithBackgroundColor} again")
            player.addActions(1)
            if (card.isDuration) {
                card.durationCardCopiedByCitadel = true
            }
            player.playCard(card, repeatedAction = true, showLog = false)
        }
    }

    companion object {
        const val NAME: String = "Citadel"
    }
}