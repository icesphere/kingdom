package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Rebuild : DarkAgesCard(NAME, CardType.Action, 5), ChooseCardActionCard {

    init {
        addActions = 1
        special = "Name a card. Reveal cards from your deck until you reveal a Victory card you did not name. Discard the rest, trash the Victory card, and gain a Victory card costing up to \$3 more than it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cardsToSelectFrom = player.game.allCardsCopy
        player.chooseCardAction(special, this, cardsToSelectFrom, false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addEventLogWithUsername("named ${card.cardNameWithBackgroundColor}")

        val victoryCard = player.revealFromDeckUntilCardFoundAndDiscardOthers { c -> c.isVictory && c.name != card.name }
        if (victoryCard != null) {
            player.cardTrashed(victoryCard, true)
            player.chooseSupplyCardToGain(player.getCardCostWithModifiers(victoryCard) + 3, { c -> c.isVictory })
        } else {
            player.showInfoMessage("You had no victory cards in your deck that are not ${card.cardNameWithBackgroundColor}")
            player.addEventLogWithUsername("had no victory cards in their deck that are not ${card.cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Rebuild"
    }
}

