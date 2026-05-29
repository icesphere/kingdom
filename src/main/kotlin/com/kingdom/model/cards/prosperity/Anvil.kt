package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Anvil : ProsperityCard(NAME, CardType.Treasure, 3), ChoiceActionCard, DiscardCardsForBenefitActionCard {

    init {
        addCoins = 1
        special = "You may discard a Treasure to gain a card costing up to \$4."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isTreasure }) {
            player.yesNoChoice(this, "Discard a Treasure to gain a card costing up to \$4?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardCardsForBenefit(this, 1, "Discard a Treasure to gain a card costing up to \$4") { it.isTreasure }
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        if (discardedCards.isNotEmpty()) {
            player.chooseSupplyCardToGainWithMaxCost(4)
        }
    }

    companion object {
        const val NAME: String = "Anvil"
    }
}
