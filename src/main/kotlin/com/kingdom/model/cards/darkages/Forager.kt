package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Forager : DarkAgesCard(NAME, CardType.Action, 3), TrashCardsForBenefitActionCard {

    init {
        addActions = 1
        addBuys = 1
        special = "Trash a card from your hand, then +\$1 per differently named Treasure in the trash."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        val numDifferentTreasuresInTrash = player.game.trashedCards.filter { it.isTreasure }.groupBy { it.name }.size
        if (numDifferentTreasuresInTrash == 1) {
            player.addGameLog("There was 1 differently named treasure in the trash")
        } else {
            player.addGameLog("There were $numDifferentTreasuresInTrash differently named treasures in the trash")
        }
        if (numDifferentTreasuresInTrash > 0) {
            player.addCoins(numDifferentTreasuresInTrash)
        }
    }

    companion object {
        const val NAME: String = "Forager"
    }
}

