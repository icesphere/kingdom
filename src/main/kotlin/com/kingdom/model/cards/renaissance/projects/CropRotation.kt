package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.players.Player

class CropRotation : RenaissanceProject(NAME, 6), StartOfTurnProject, ChoiceActionCard, DiscardCardsForBenefitActionCard {

    init {
        special = "At the start of your turn, you may discard a Victory card for +2 Cards."
        fontSize = 10
    }

    override fun onStartOfTurn(player: Player) {
        if (player.hand.any { it.isVictory }) {
            player.yesNoChoice(this, "Discard a Victory card for +2 Cards?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardCardsForBenefit(this, 1, "Discard a Victory card") { c -> c.isVictory }
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        player.drawCards(2)
    }

    companion object {
        const val NAME: String = "Crop Rotation"
    }
}