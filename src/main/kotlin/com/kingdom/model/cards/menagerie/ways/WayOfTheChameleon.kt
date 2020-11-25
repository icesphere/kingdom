package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class WayOfTheChameleon : MenagerieWay(NAME) {

    init {
        special = "Follow this cards instructions; each time that would give you +Cards this turn, you get +Coins instead, and vice-versa."
    }

    override fun waySpecialAction(player: Player, card: Card) {
        player.isSwapCardsAndCoins = true
        player.addActions(1)
        card.cardPlayed(player, ignoreWays = true)
        player.isSwapCardsAndCoins = false
    }

    companion object {
        const val NAME: String = "Way of the Chameleon"
    }

}