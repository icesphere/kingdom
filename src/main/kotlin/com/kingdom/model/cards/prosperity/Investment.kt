package com.kingdom.model.cards.prosperity

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Investment : ProsperityCard(NAME, CardType.Treasure, 4), ChoiceActionCard, TrashCardsForBenefitActionCard {

    init {
        special = "Trash a card from your hand. Choose one: +\$1; or trash this to reveal your hand for +1 VP per differently named Treasure there."
        isTreasureExcludedFromAutoPlay = true
        isTrashingCard = true
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand")
        } else {
            chooseBenefit(player)
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        chooseBenefit(player)
    }

    private fun chooseBenefit(player: Player) {
        player.makeChoice(this, Choice(1, "+\$1"), Choice(2, "Trash this for VP"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addCoins(1)
            return
        }

        if (player.inPlay.contains(this)) {
            player.trashCardInPlay(this)
        }

        player.revealHand()
        val victoryCoins = player.hand.filter { it.isTreasure }.distinctBy { it.name }.count()
        player.addVictoryCoins(victoryCoins, true)
    }

    companion object {
        const val NAME: String = "Investment"
    }
}
