package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.OptionalChooseCardActionCard
import com.kingdom.model.cards.listeners.AfterShuffleListener
import com.kingdom.model.players.Player

class StarChart : RenaissanceProject(NAME, 3), AfterShuffleListener, OptionalChooseCardActionCard {

    //todo figure out how to handle stopping other actions while waiting for the player to choose which cards to put on top of the deck

    init {
        disabled = true
        special = "When you shuffle, you may pick one of the cards to go on top."
    }

    override fun afterShuffle(player: Player) {
        player.chooseCardAction("You may choose a card to go on top of your deck", this, player.deckCopy, true)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.deck.remove(card)
        player.deck.shuffle()
        player.addCardToTopOfDeck(card, false)
        player.addEventLogWithUsername("picked a card to go on top of their deck after shuffling")
    }

    override fun onCardNotChosen(player: Player, info: Any?) {
        player.deck.shuffle()
    }

    companion object {
        const val NAME: String = "Star Chart"
    }
}