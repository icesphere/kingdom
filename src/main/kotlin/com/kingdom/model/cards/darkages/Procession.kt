package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.listeners.CardPlayedListenerForCardsInPlay
import com.kingdom.model.players.Player

class Procession : DarkAgesCard(NAME, CardType.Action, 4), ChooseCardActionCardOptional, CardPlayedListenerForCardsInPlay {

    var cardToPlayTwice: Card? = null

    var numTimesCardPlayed: Int = 0

    init {
        special = "You may play an Action card from your hand twice. Trash it. Gain an Action card costing exactly \$1 more than it."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHandOptional("Choose an Action card from your hand to play twice", this, { c -> c.isAction })
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        cardToPlayTwice = card

        if (card != null) {
            player.addActions(1)
            player.playCard(card)
            player.addRepeatCardAction(card)
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
        super.removedFromPlay(player)
        cardToPlayTwice = null
        numTimesCardPlayed = 0
    }

    companion object {
        const val NAME: String = "Procession"
    }
}

