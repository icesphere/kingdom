package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class PutCardsOnTopOfDeckInAnyOrder(cards: List<Card>) : SelectCardsAction("Select cards in the order you want them to go on top of your deck (last card selected will be on the top)", cards, cards.size, false) {

    override val isShowDone: Boolean = false

    override fun processActionResult(player: Player, result: ActionResult): Boolean {

        val selectedCard = result.selectedCard!!

        player.addCardToTopOfDeck(selectedCard)

        cardChoices = cardChoices?.filterNot { it.id == selectedCard.id }

        return cardChoices?.isEmpty() ?: true
    }

    override fun isCardSelected(card: Card): Boolean {
        return selectedCards.contains(card)
    }
}