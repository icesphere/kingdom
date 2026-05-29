package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class WarChest : ProsperityCard(NAME, CardType.Treasure, 5), ChooseCardActionCard {

    init {
        special = "The player to your left names a card. Gain a card costing up to \$5 that has not been named for War Chests this turn."
        isTreasureExcludedFromAutoPlay = true
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.playerToLeft.chooseCardAction(
                "Choose a card that ${player.username} cannot gain with ${this.cardNameWithBackgroundColor} this turn",
                this,
                player.game.allCardsCopy.sortedBy { it.cost },
                false,
                player
        )
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        val warChestPlayer = info as? Player

        if (warChestPlayer != null) {
            warChestPlayer.warChestCardNamesThisTurn.add(card.name)
            warChestPlayer.chooseCardFromSupply(
                    "Gain a card costing up to \$5 that has not been named for War Chests this turn",
                    this,
                    { it.debtCost == 0 && warChestPlayer.getCardCostWithModifiers(it) <= 5 && !warChestPlayer.warChestCardNamesThisTurn.contains(it.name) },
                    null,
                    false
            )
        } else {
            player.gainSupplyCard(card, true)
        }
    }

    companion object {
        const val NAME: String = "War Chest"
    }
}
