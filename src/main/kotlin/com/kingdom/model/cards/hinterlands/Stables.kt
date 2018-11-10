package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Stables : HinterlandsCard(NAME, CardType.Action, 5), ChoiceActionCard, DiscardCardsForBenefitActionCard {

    init {
        special = "You may discard a Treasure, for +3 Cards and +1 Action."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isTreasure }) {
            player.yesNoChoice(this, "Discard a Treasure for +3 Cards and +1 Action?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardCardsForBenefit(this, 1, "Discard a Treasure card", { c -> c.isTreasure })
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>) {
        player.drawCards(3)
        player.addActions(1)
    }

    override fun onChoseDoNotUse(player: Player) {
        //do nothing
    }

    companion object {
        const val NAME: String = "Stables"
    }
}

