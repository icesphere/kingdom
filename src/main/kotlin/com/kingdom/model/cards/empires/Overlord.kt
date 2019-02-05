package com.kingdom.model.cards.empires

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Overlord : EmpiresCard(NAME, CardType.Action, 0, 8), ChooseCardActionCard {

    init {
        special = "Play this as if it were an Action card in the Supply costing up to \$5. This is that card until it leaves play."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.game.availableCardsCopy.filter { it.isAction && it.debtCost == 0 && player.getCardCostWithModifiers(it) <= 5 }
        if (cards.isNotEmpty()) {
            player.chooseCardAction("Choose an Action card from the Supply costing up to \$5. ${this.cardNameWithBackgroundColor} will be that card until it leaves play.", this, cards, false)
        } else {
            player.addEventLog("There were no Action cards in the Supply costing up to \$5")
            player.showInfoMessage("There were no Action cards in the Supply costing up to \$5")
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardInPlay(this)
        val overlordCard = player.game.getNewInstanceOfCard(card.name)
        overlordCard.isCardActuallyOverlord = true
        player.addActions(1)
        player.playCard(overlordCard)
    }

    companion object {
        const val NAME: String = "Overlord"
    }
}

