package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class ChooseCardToGainFromTrash(cardsInTrash: List<Card>, optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)? = null) : SelectCardsFromCardAction("", cardsInTrash, 1, optional, cardActionableExpression) {

    init {
        text = "Choose a card to gain from the trash"
    }

    override fun onSelectionDone(player: Player) {
        val card = selectedCards.first()

        player.game.trashedCards.remove(card)
        player.addGameLog("${player.username} gained ${card.cardNameWithBackgroundColor} from the trash")
        player.cardGained(card)
    }

}