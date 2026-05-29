package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForCardsInPlay
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Hoard : ProsperityCard(NAME, CardType.Treasure, 6), AfterCardBoughtListenerForCardsInPlay {

    init {
        addCoins = 2
        special = "This turn, when you gain a Victory card, if you bought it, gain a Gold."
    }

    override fun afterCardBought(card: Card, player: Player) {
        if (card.isVictory) {
            player.gainSupplyCard(Gold(), true)
            player.showInfoMessage("Gained ${Gold().cardNameWithBackgroundColor} from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Hoard"
    }
}
