package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

open class FreeCardFromSupply(text: String, private val cardActionableExpression: ((card: Card) -> Boolean)?, private val destination: CardLocation = CardLocation.Discard, optional: Boolean = false) : Action(text) {

    override var isShowDoNotUse: Boolean = optional

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Supply
                && player.game.isCardAvailableInSupply(card)
                && (cardActionableExpression == null || cardActionableExpression.invoke(card))
    }

    override fun processAction(player: Player): Boolean {
        return player.game.availableCards.any { cardActionableExpression == null || cardActionableExpression.invoke(it) }
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!

        player.game.removeCardFromSupply(card)

        player.addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} from the supply")

        when (destination) {
            CardLocation.Hand -> player.gainCardToHand(card)
            CardLocation.Deck -> player.gainCardToTopOfDeck(card)
            else -> player.cardGained(card)
        }

        return true
    }
}