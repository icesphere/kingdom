package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Mandarin : HinterlandsCard(NAME, CardType.Action, 5), AfterCardGainedListenerForSelf {

    init {
        addCoins = 3
        special = "Put a card from your hand on top of your deck. When you gain this, put all Treasures you have in play onto your deck in any order."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCardFromHandToTopOfDeck()
    }

    override fun afterCardGained(player: Player) {
        val treasuresInPlay = player.inPlay.filter { it.isTreasure }
        treasuresInPlay.forEach { player.removeCardInPlay(it, CardLocation.Deck) }
        player.putCardsOnTopOfDeckInAnyOrder(treasuresInPlay)
        player.addEventLogWithUsername("added ${treasuresInPlay.groupedString} to the top of their deck")
    }

    companion object {
        const val NAME: String = "Mandarin"
    }
}

