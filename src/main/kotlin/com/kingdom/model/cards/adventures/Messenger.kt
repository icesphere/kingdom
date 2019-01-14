package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.players.Player

class Messenger : AdventuresCard(NAME, CardType.Action, 4), ChoiceActionCard, AfterCardBoughtListenerForSelf, FreeCardFromSupplyForBenefitActionCard {

    init {
        addBuys = 1
        addCoins = 2
        special = "You may put your deck into your discard pile. When this is your first buy in a turn, gain a card costing up to \$4, and each other player gains a copy of it."
        textSize = 83
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.yesNoChoice(this, "Put your deck into your discard pile?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.putDeckIntoDiscard()
        }
    }

    override fun afterCardBought(player: Player) {
        if (player.game.cardsBought.size == 1) {
            player.chooseSupplyCardToGainForBenefit(4, "Gain a card costing up to \$4, and each other player gains a copy of it.", this)
        }
    }

    override fun onCardGained(player: Player, card: Card) {
        player.opponents.forEach { opponent ->
            opponent.gainSupplyCard(card, true)
            opponent.showInfoMessage("You gained ${card.cardNameWithBackgroundColor} from ${player.username}'s ${this.cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Messenger"
    }
}

