package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class BandOfMisfits : DarkAgesCard(NAME, CardType.Action, 5), ChooseCardActionCard {

    init {
        special = "Play this as if it were a cheaper Action card in the Supply. This is that card until it leaves play."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.game.availableCards.filter { it.isAction && player.getCardCostWithModifiers(it) < player.getCardCostWithModifiers(this) }
        if (cards.isNotEmpty()) {
            player.chooseCardAction("Choose a cheaper Action card from the Supply. ${this.cardNameWithBackgroundColor} will be that card until it leaves play.", this, cards, false)
        } else {
            player.addGameLog("There were no cheaper Action cards in the Supply")
            player.showInfoMessage("There are no cheaper Action cards")
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardInPlay(this)
        val bandOfMisfitsCard = player.game.getSupplyCard(card.name)
        bandOfMisfitsCard.isCardActuallyBandOfMisfits = true
        player.addActions(1)
        player.playCard(bandOfMisfitsCard)
    }

    companion object {
        const val NAME: String = "Band of Misfits"
    }
}

