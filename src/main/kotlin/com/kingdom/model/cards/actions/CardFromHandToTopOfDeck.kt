package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

//todo change text if cardFilter specified
class CardFromHandToTopOfDeck(private val cardFilter: ((Card) -> Boolean)?,
                              private val chooseCardActionCard: ChooseCardActionCard?) : Action("Choose a card from your hand to put on top of your deck") {

    override var isShowDoNotUse: Boolean = false

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Hand && cardFilter?.invoke(card) ?: true
    }

    override fun processAction(player: Player): Boolean {
        return player.hand.any { cardFilter == null || cardFilter.invoke(it) }
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!
        player.hand.remove(card)
        player.addCardToTopOfDeck(card)
        chooseCardActionCard?.onCardChosen(player, card)
        return true
    }
}