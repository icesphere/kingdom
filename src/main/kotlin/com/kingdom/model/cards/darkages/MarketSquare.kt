package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForCardsInHand
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class MarketSquare : DarkAgesCard(NAME, CardType.ActionReaction, 3), AfterCardTrashedListenerForCardsInHand, ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        addBuys = 1
        special = "When one of your cards is trashed, you may discard this from your hand to gain a Gold."
        fontSize = 9
    }

    override fun afterCardTrashed(card: Card, player: Player) {
        player.yesNoChoice(this, "Discard ${this.cardNameWithBackgroundColor} from your hand to gain a ${Gold().cardNameWithBackgroundColor}?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardCardFromHand(this, true)
            player.gainSupplyCard(Gold(), true)
        }
    }

    companion object {
        const val NAME: String = "Market Square"
    }
}

