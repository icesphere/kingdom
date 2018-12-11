package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class TreasureTrove : AdventuresCard(NAME, CardType.Treasure, 5) {

    init {
        addCoins = 2
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        fontSize = 9
        special = "When you play this, gain a Gold and a Copper."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Gold(), true)
        player.gainSupplyCard(Copper(), true)
    }

    companion object {
        const val NAME: String = "Treasure Trove"
    }
}

