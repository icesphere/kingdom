package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForCardsInTavern
import com.kingdom.model.players.Player

class Duplicate : AdventuresCard(NAME, CardType.ActionReserve, 4), TavernCard, AfterCardGainedListenerForCardsInTavern, ChoiceActionCard {

    init {
        special = "Put this on your Tavern mat. When you gain a card costing up to \$6, you may call this, to gain a copy of that card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.moveCardInPlayToTavern(this)
    }

    override fun isTavernCardActionable(player: Player): Boolean = false

    override fun onTavernCardCalled(player: Player) {
        //handled when card gained
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (player.getCardCostWithModifiers(card) <= 6 && player.game.isCardAvailableInSupply(card)) {
            player.yesNoChoice(this, "Call ${this.cardNameWithBackgroundColor} from Tavern to gain a copy of ${card.cardNameWithBackgroundColor}?", card)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.moveCardInTavernToInPlay(this)
            player.gainSupplyCard(info as Card, true)
        }
    }

    companion object {
        const val NAME: String = "Duplicate"
    }
}

