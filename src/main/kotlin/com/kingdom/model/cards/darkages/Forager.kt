package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Forager : DarkAgesCard(NAME, CardType.Action, 3), TrashCardsForBenefitActionCard {

    init {
        addActions = 1
        addBuys = 1
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        isAddCoinsCard = true
        special = "Trash a card from your hand, then +\$1 per differently named Treasure in the trash."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val numDifferentTreasuresInTrash = player.game.trashedCards.filter { it.isTreasure }.groupBy { it.name }.size

        val message = if (numDifferentTreasuresInTrash == 1) {
            "There was 1 differently named treasure in the trash"
        } else {
            "There were $numDifferentTreasuresInTrash differently named treasures in the trash"
        }
        player.addEventLog(message)
        player.showInfoMessage(message)

        if (numDifferentTreasuresInTrash > 0) {
            player.addCoins(numDifferentTreasuresInTrash)
        }
    }

    companion object {
        const val NAME: String = "Forager"
    }
}

