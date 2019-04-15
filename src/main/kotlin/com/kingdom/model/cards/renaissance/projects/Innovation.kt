package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListener
import com.kingdom.model.players.Player

class Innovation : RenaissanceProject(NAME, 6), AfterCardGainedListener, ChoiceActionCard {

    init {
        special = "The first time you gain an Action card in each of your turns, you may set it aside. If you do, play it."
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.isAction && player.currentTurnSummary.cardsGained.count { it.isAction } == 1 &&
                (player.cardsInDiscard.contains(card) || player.deck.contains(card) || player.hand.contains(card))) {
            player.yesNoChoice(this, "Set aside and play ${card.cardNameWithBackgroundColor}?", card)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val card = info as Card
            when {
                player.cardsInDiscard.contains(card) -> player.removeCardFromDiscard(card)
                player.deck.contains(card) -> player.removeCardFromDeck(card)
                player.hand.contains(card) -> player.removeCardFromHand(card)
            }
            player.addEventLogWithUsername("$cardNameWithBackgroundColor played ${card.cardNameWithBackgroundColor}")
            player.addActions(1)
            player.playCard(card)
        }
    }

    companion object {
        const val NAME: String = "Innovation"
    }
}