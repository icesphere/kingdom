package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Bureaucrat : KingdomCard(NAME, CardType.ActionAttack, 4) {
    init {
        special = "Gain a silver card; put it on top of your deck. Each other player reveals a Victory card from his hand and puts it on his deck (or reveals a hand with no Victory cards)."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.isNextCardToTopOfDeck = true
        player.acquireFreeCardFromSupply(Silver())
        //todo
    }

    companion object {
        const val NAME: String = "Bureaucrat"
    }
}

