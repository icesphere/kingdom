package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.players.Player

class SilkMerchant : RenaissanceCard(NAME, CardType.Action, 4), AfterCardGainedListenerForSelf, AfterCardTrashedListenerForSelf {

    init {
        addCards = 2
        addBuys = 1
        special = "When you gain or trash this, +1 Coffers and +1 Villager."
        fontSize = 10
    }

    override fun afterCardGained(player: Player) {
        gainStuff(player)
    }

    override fun afterCardTrashed(player: Player) {
        gainStuff(player)
    }

    private fun gainStuff(player: Player) {
        player.addCoffers(1)
        player.addVillagers(1)
        player.addEventLogWithUsername("gained +1 Coffers and +1 Villager from $cardNameWithBackgroundColor")
    }

    companion object {
        const val NAME: String = "Silk Merchant"
    }
}