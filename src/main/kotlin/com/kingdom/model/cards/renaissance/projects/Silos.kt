package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.players.Player

class Silos : RenaissanceProject(NAME, 4), StartOfTurnProject, ChooseCardsActionCard {

    init {
        special = "At the start of your turn, discard any number of Coppers, revealed, and draw that many cards."
    }

    override fun onStartOfTurn(player: Player) {
        val coppers = player.hand.count { it.isCopper }
        if (coppers > 0) {
            player.chooseCardsFromHand("Choose any number of Coppers to reveal, then draw that many cards", coppers, true, this, { c -> c.isCopper })
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        player.addEventLogWithUsername("revealed ${cards.size} ${Copper().cardNameWithBackgroundColor}")
        player.drawCards(cards.size)
    }

    companion object {
        const val NAME: String = "Silos"
    }
}