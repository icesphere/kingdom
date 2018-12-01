package com.kingdom.model.cards.guilds

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Butcher : GuildsCard(NAME, CardType.Action, 5), TrashCardsForBenefitActionCard, ChoiceActionCard {

    init {
        addCoffers = 2
        special = "You may trash a card from your hand. If you do, remove any number of tokens from your Coffers and gain a card, costing up to the cost of the trashed card plus \$1 per token removed."
        textSize = 93
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        if (trashedCards.isNotEmpty()) {
            if (player.coffers == 0) {
                player.chooseSupplyCardToGain(player.getCardCostWithModifiers(trashedCards[0]))
                return
            }

            val choices = mutableListOf<Choice>()

            for (i in 0..player.coffers) {
                choices.add(Choice(i, i.toString()))
            }

            player.makeChoiceFromListWithInfo(this, "How many Coffers to you want to remove?", trashedCards[0], choices)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val trashedCard = info as Card

        player.addCoffers(choice * -1)
        player.chooseSupplyCardToGain(player.getCardCostWithModifiers(trashedCard) + choice)
    }

    companion object {
        const val NAME: String = "Butcher"
    }
}

