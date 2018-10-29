package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class ChooseCardToGainFromTrash(cardsInTrash: List<Card>, optional: Boolean, private val cardActionableExpression: ((card: Card) -> Boolean)? = null) : SelectCardsAction("", cardsInTrash, 1, optional) {

    override val isShowDone: Boolean = false

    init {
        this.isShowDoNotUse = optional

        text = "Choose a card to gain from the trash"
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Trash && (cardActionableExpression == null || cardActionableExpression.invoke(card))
    }

    override fun processAction(player: Player): Boolean {
        return super.processAction(player) && player.game.trashedCards.any { cardActionableExpression == null || cardActionableExpression.invoke(it) }
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!
        player.game.trashedCards.remove(card)
        player.addGameLog("${player.username} gained ${card.cardNameWithBackgroundColor} from the trash")
        player.cardAcquired(card)
        return true
    }

}