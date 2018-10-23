package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Island : SeasideCard(NAME, CardType.ActionVictory, 4), ChooseCardActionCard {

    init {
        testing = true
        victoryPoints = 2
        special = "Put this and a card from your hand onto your Island mat."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand("Choose a card from your hand to put on your Island mat", this)
    }

    override fun onCardChosen(player: Player, card: Card) {
        player.revealCardFromHand(card)
        player.islandCards.add(card)
    }

    companion object {
        const val NAME: String = "Island"
    }
}

