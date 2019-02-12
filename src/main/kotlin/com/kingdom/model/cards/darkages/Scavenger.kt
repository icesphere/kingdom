package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Scavenger : DarkAgesCard(NAME, CardType.Action, 4), ChoiceActionCard, ChooseCardActionCard {

    init {
        addCoins = 2
        special = "You may put your deck into your discard pile. Look through your discard pile and put one card from it onto your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.yesNoChoice(this, "Put your deck into your discard pile?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.putDeckIntoDiscard()
        }
        if (player.cardsInDiscard.isNotEmpty()) {
            player.chooseCardAction("Look through your discard pile and put one card from it onto your deck.", this, player.cardsInDiscardCopy, false)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardFromDiscard(player.cardsInDiscard.first { it.name == card.name })
        player.addCardToTopOfDeck(card)
    }

    companion object {
        const val NAME: String = "Scavenger"
    }
}

