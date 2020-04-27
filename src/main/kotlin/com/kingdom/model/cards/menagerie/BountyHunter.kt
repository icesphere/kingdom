package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class BountyHunter : MenagerieCard(NAME, CardType.Action, 4), ChooseCardActionCard, UsesExileMat {

    init {
        addActions = 1
        special = "Exile a card from your hand. If you didn’t have a copy of it in Exile, +\$3."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand(special, this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        val copyInExile = player.exileCards.any { it.name == card.name }
        player.exileCardFromHand(card)
        if (!copyInExile) {
            player.addCoins(3)
            player.addEventLogWithUsername("gained +\$3 from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Bounty Hunter"
    }
}

