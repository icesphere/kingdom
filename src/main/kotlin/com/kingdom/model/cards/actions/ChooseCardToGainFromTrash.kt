package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class ChooseCardToGainFromTrash(cardsInTrash: List<Card>, optional: Boolean) : SelectCardsAction("", cardsInTrash, 1, optional) {

    override val isShowDone: Boolean = false

    init {
        this.isShowDoNotUse = optional

        text = "Choose a card to gain from the trash"
    }

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean {
        return cardLocation == CardLocation.Trash
    }

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        val card = result.selectedCard!!
        player.addGameLog("${player.username} gained ${card.cardNameWithBackgroundColor} from the trash")
        player.cardAcquired(card)
        return true
    }

}