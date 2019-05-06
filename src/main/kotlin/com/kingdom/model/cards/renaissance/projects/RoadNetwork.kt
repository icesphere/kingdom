package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.listeners.AfterOtherPlayerCardGainedListenerForProjects
import com.kingdom.model.players.Player

class RoadNetwork : RenaissanceProject(NAME, 5), AfterOtherPlayerCardGainedListenerForProjects {

    init {
        special = "When another player gains a Victory card, +1 Card."
        fontSize = 10
    }

    override fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        if (card.isVictory) {
            player.addEventLogWithUsername("gained +1 Card from $cardNameWithBackgroundColor")
            player.showInfoMessage("Gained +1 Card from $cardNameWithBackgroundColor")
            player.drawCard()
        }
    }

    companion object {
        const val NAME: String = "Road Network"
    }
}