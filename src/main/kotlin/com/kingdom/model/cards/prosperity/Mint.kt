package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.listeners.CardBoughtListenerForSelf
import com.kingdom.model.players.Player

class Mint : ProsperityCard(NAME, CardType.Action, 5), CardBoughtListenerForSelf, ChooseCardActionCard {

    init {
        special = "You may reveal a Treasure card from your hand. Gain a copy of it. When you buy this, trash all Treasures you have in play."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val treasureCards = player.hand.filter { it.isTreasure }
        if (treasureCards.isNotEmpty()) {
            player.chooseCardFromHand("Chose a Treasure card to gain a copy of", this, { c -> c.isTreasure })
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.acquireFreeCardFromSupply(card, true)
    }

    override fun onCardBought(player: Player): Boolean {
        player.inPlay.filter { it.isTreasure }.forEach {
            player.trashCardInPlay(it)
        }
        return false
    }

    companion object {
        const val NAME: String = "Mint"
    }
}

