package com.kingdom.model.cards.adventures

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Raze : AdventuresCard(NAME, CardType.Action, 2), ChoiceActionCard, TrashCardsForBenefitActionCard, ChooseCardActionCard {

    init {
        addActions = 1
        special = "Trash this or a card from your hand. Look at one card from your deck per \$1 the trashed card costs. Put one of them into your hand and discard the rest."
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isEmpty()) {
            player.trashCardInPlay(this)
            handleTrashedCard(player, this)
        } else {
            player.makeChoice(this, Choice(1, "Trash Raze"), Choice(2, "Trash from hand"))
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardInPlay(this)
            handleTrashedCard(player, this)
        } else {
            player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand. Look at one card from your deck per \$1 the trashed card costs. Put one of them into your hand and discard the rest.")
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        handleTrashedCard(player, trashedCards.first())
    }

    private fun handleTrashedCard(player: Player, trashedCard: Card) {
        val cost = player.getCardCostWithModifiers(trashedCard)
        if (cost > 0) {
            val cards = player.removeTopCardsOfDeck(cost)
            if (cards.isNotEmpty()) {
                player.chooseCardAction("Choose one to put into your hand, the rest will be discarded.", this, cards, false, cards)
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        @Suppress("UNCHECKED_CAST")
        val cards = info as List<Card>

        player.addCardToHand(card, true)
        val cardsToDiscard = cards - card
        if (cardsToDiscard.isNotEmpty()) {
            player.addCardsToDiscard(cardsToDiscard, true)
        }
    }

    companion object {
        const val NAME: String = "Raze"
    }
}

