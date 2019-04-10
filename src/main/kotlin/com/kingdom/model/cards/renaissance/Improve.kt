package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.CardRepeater
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.cards.actions.OptionalChooseCardActionCard
import com.kingdom.model.cards.listeners.StartOfCleanupListener
import com.kingdom.model.players.Player

class Improve : RenaissanceCard(NAME, CardType.Action, 3), StartOfCleanupListener, OptionalChooseCardActionCard, FreeCardFromSupplyForBenefitActionCard {

    var usedImprove: Boolean = false

    init {
        addCoins = 2
        special = "At the start of Clean-up, you may trash an Action card you would discard from play this turn, to gain a card costing exactly \$1 more than it."
        isTrashingCard = true
    }

    override fun onStartOfCleanup(player: Player) {
        if (usedImprove) {
            return
        }

        usedImprove = true

        val durationCardsToDiscard = player.durationCards.filterNot {
            it is PermanentDuration
                    || (it is CardRepeater && it.cardBeingRepeated is PermanentDuration)
                    || (it is MultipleTurnDuration && it.keepAtEndOfTurn(player)
                    || (it is CardRepeater && it.cardBeingRepeated is MultipleTurnDuration && (it.cardBeingRepeated as MultipleTurnDuration).keepAtEndOfTurn(player)))
        }

        val inPlayToDiscard = player.inPlay.filterNot { card -> card.isDuration || (card is CardRepeater && card.cardBeingRepeated?.isDuration == true) }

        val actionCardsToDiscard = (durationCardsToDiscard + inPlayToDiscard).filter { it.isAction }.map { it.copy(false) }

        if (actionCardsToDiscard.isNotEmpty()) {
            player.chooseCardAction("You may trash an Action card you would discard from play this turn, to gain a card costing exactly \$1 more than it", this, actionCardsToDiscard, true)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        if (player.inPlay.any { it.name == card.name }) {
            player.removeCardInPlay(player.inPlay.first { it.name == card.name }, CardLocation.Trash)
        } else {
            player.removeDurationCardInPlay(player.durationCards.first { it.name == card.name }, CardLocation.Trash)
        }

        val exactCost = player.getCardCostWithModifiers(card) + 1
        if (player.game.availableCards.any { player.getCardCostWithModifiers(it) == exactCost }) {
            player.chooseSupplyCardToGainForBenefitWithExactCost(exactCost, "Gain a free card from the supply costing exactly $exactCost", this)
        } else {
            player.showInfoMessage("There were no cards available that cost \$$exactCost")
        }
    }

    override fun onCardNotChosen(player: Player, info: Any?) {
        player.endTurn(true)
    }

    override fun onCardGained(player: Player, card: Card) {
        player.endTurn(true)
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        usedImprove = false
    }

    companion object {
        const val NAME: String = "Improve"
    }
}