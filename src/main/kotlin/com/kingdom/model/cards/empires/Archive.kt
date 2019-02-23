package com.kingdom.model.cards.empires

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.MultipleTurnDuration
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Archive : EmpiresCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction, MultipleTurnDuration, ChooseCardActionCard {

    var setAsideCards = mutableListOf<Card>()

    init {
        addActions = 1
        special = "Set aside the top 3 cards of your deck face down (you may look at them). Now and at the start of your next two turns, put one into your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        setAsideCards = player.removeTopCardsOfDeck(3).toMutableList()

        chooseCardToAddToHand(player)
    }

    override fun durationStartOfTurnAction(player: Player) {
        chooseCardToAddToHand(player)
    }

    private fun chooseCardToAddToHand(player: Player) {
        if (setAsideCards.isNotEmpty()) {
            player.chooseCardAction("Choose a card from $cardNameWithBackgroundColor to add to your hand", this, setAsideCards, false)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        setAsideCards.remove(card)
        player.addCardToHand(card)
    }

    override fun keepAtEndOfTurn(player: Player): Boolean {
        return setAsideCards.isNotEmpty()
    }

    companion object {
        const val NAME: String = "Archive"
    }
}

