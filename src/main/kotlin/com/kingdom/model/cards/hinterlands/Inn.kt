package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Inn : HinterlandsCard(NAME, CardType.Action, 5), AfterCardGainedListenerForSelf, ChooseCardsActionCard {

    init {
        addCards = 2
        addActions = 2
        special = "Discard 2 cards. When you gain this, look through your discard pile, reveal any number of Action cards from it (which can include this), and shuffle them into your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsFromHand(2, false)
    }

    override fun afterCardGained(player: Player) {
        val actionCardsInDiscard = player.cardsInDiscard.filter { it.isAction }
        if (actionCardsInDiscard.isNotEmpty()) {
            player.chooseCardsAction(actionCardsInDiscard.size, "Reveal any number of Action cards from your discard pile to shuffle into your deck", this, actionCardsInDiscard, true)
        } else {
            player.showInfoMessage("There were no Action cards in your discard pile. Discard pile contained: ${player.cardsInDiscard.groupedString}")
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        if (cards.isNotEmpty()) {
            player.removeCardsFromDiscard(cards)
            player.addCardsToDeck(cards)
            player.deck.shuffle()
            player.addEventLogWithUsername("shuffled ${cards.groupedString} into their deck")
        }
    }

    companion object {
        const val NAME: String = "Inn"
    }
}

