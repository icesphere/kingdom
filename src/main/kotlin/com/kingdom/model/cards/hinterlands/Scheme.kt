package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.OptionalChooseCardActionCard
import com.kingdom.model.cards.listeners.StartOfCleanupListener
import com.kingdom.model.players.Player

class Scheme : HinterlandsCard(NAME, CardType.Action, 3), StartOfCleanupListener, OptionalChooseCardActionCard {

    private var usedOnGameTurn: Int? = null

    init {
        addCards = 1
        addActions = 1
        special = "This turn, you may put one of your Action cards onto your deck when you discard it from play."
        fontSize = 11
    }

    override fun onStartOfCleanup(player: Player) {
        if (usedOnGameTurn == player.game.turn) {
            return
        }

        usedOnGameTurn = player.game.turn
        val actionCardsInPlay = player.inPlay.filter { it.isAction }.map { it.copy(false) }
        if (actionCardsInPlay.isNotEmpty()) {
            player.chooseCardAction("Put an Action card you would discard from play onto your deck", this, actionCardsInPlay, true)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.inPlay.firstOrNull { it.name == card.name }?.let {
            player.removeCardInPlay(it, CardLocation.Deck)
            player.addCardToTopOfDeck(it, false)
        }
        player.endTurn(true)
    }

    override fun onCardNotChosen(player: Player, info: Any?) {
        player.endTurn(true)
    }

    companion object {
        const val NAME: String = "Scheme"
    }
}
