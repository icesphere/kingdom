package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Swashbuckler : RenaissanceCard(NAME, CardType.Action, 5) {

    init {
        disabled = true
        addCards = 3
        special = "If your discard pile has any cards in it: +1 Coffers, then if you have at least 4 Coffers tokens, take the Treasure Chest."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.cardsInDiscard.isNotEmpty()) {
            player.addCoffers(1)
            player.addEventLogWithUsername("gained +1 Coffers from $cardNameWithBackgroundColor")

            if (player.coffers >= 4) {
                //todo take treasure chest
            }
        }
    }

    companion object {
        const val NAME: String = "Swashbuckler"
    }
}