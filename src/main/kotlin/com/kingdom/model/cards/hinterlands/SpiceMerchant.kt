package com.kingdom.model.cards.hinterlands

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class SpiceMerchant : HinterlandsCard(NAME, CardType.Action, 4), ChoiceActionCard, TrashCardsForBenefitActionCard {

    init {
        isAddCoinsCard = true
        special = "You may trash a Treasure from your hand. If you do, choose one: +2 Cards and +1 Action; or +1 Buy and \$2."
        fontSize = 9
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isTreasure }) {
            player.yesNoChoice(this, "Trash a Treasure from your hand to get +2 Cards and +1 Action or +1 Buy and \$2?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> player.trashCardsFromHandForBenefit(this, 1, "Trash a Treasure from your hand", { c -> c.isTreasure })
            3 -> {
                player.drawCards(2)
                player.addActions(1)
            }
            4 -> {
                player.addBuys(1)
                player.addCoins(2)
            }
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        player.makeChoice(this, Choice(3, "+2 Cards and +1 Action"), Choice(4, "+1 Buy and \$2"))
    }

    companion object {
        const val NAME: String = "Spice Merchant"
    }
}

