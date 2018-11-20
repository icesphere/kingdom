package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ActionResult
import com.kingdom.model.cards.actions.CardAction
import com.kingdom.model.cards.actions.CardActionCard
import com.kingdom.model.cards.listeners.CardPlayedListenerForCardsInPlay
import com.kingdom.model.players.Player

class Procession : DarkAgesCard(NAME, CardType.Action, 4), CardActionCard, CardPlayedListenerForCardsInPlay {

    var cardToPlayTwice: Card? = null

    var numTimesCardPlayed: Int = 0

    init {
        testing = true
        special = "You may play an Action card from your hand twice. Trash it. Gain an Action card costing exactly \$1 more than it."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCardAction(this, "Choose an action card from your hand to play twice")
    }

    override fun isCardActionable(card: Card, cardAction: CardAction, cardLocation: CardLocation, player: Player): Boolean {
        return card.isAction && cardLocation == CardLocation.Hand
    }

    override fun processCardAction(player: Player): Boolean {
        return player.hand.any { it.isAction }
    }

    override fun processCardActionResult(cardAction: CardAction, player: Player, result: ActionResult) {
        result.selectedCard?.let {
            cardToPlayTwice = it
            player.addActions(1)
            player.playCard(it)
            player.addRepeatCardAction(it)
        }
    }

    override fun onCardPlayed(card: Card, player: Player) {
        if (card.id == cardToPlayTwice?.id) {
            numTimesCardPlayed++
            if (numTimesCardPlayed == 2) {
                player.trashCardInPlay(card)

                if (player.game.availableCards.any { player.getCardCostWithModifiers(it) == player.getCardCostWithModifiers(card) + 1 }) {
                    player.chooseSupplyCardToGainWithExactCost(player.getCardCostWithModifiers(card) + 1)
                }
            }
        }
    }

    override fun removedFromPlay(player: Player) {
        cardToPlayTwice = null
        numTimesCardPlayed = 0
    }

    override val isShowDoNotUse: Boolean = true

    companion object {
        const val NAME: String = "Procession"
    }
}

