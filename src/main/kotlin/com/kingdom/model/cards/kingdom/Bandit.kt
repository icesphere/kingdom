package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Bandit : KingdomCard(NAME, CardType.ActionAttack, 5) {
    init {
        special = "Gain a Gold. Each other player reveals the top two cards of their deck, trashes a revealed Treasure other than Copper, and discards the rest."
        textSize = 117
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.acquireFreeCardFromSupply(Gold())
        player.opponents.forEach {
            val topCardsOfDeck = it.revealTopCardsOfDeck(2)
        }
        //todo
    }

    companion object {
        const val NAME: String = "Bandit"
    }
}
