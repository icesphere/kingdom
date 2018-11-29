package com.kingdom.model.cards.seaside

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Island : SeasideCard(NAME, CardType.ActionVictory, 4), GameSetupModifier, ChooseCardActionCard {

    init {
        victoryPoints = 2
        special = "Put this and a card from your hand onto your Island mat."
        isTrashingCard = true
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowIslandCards = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHand("Choose a card from your hand to put on your Island mat", this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addEventLogWithUsername("added a card to their ${this.cardNameWithBackgroundColor}")
        player.removeCardFromHand(card)
        player.cardRemovedFromPlay(card)
        player.islandCards.add(card)
        player.trashCardInPlay(this, false)
        player.islandCards.add(this)
    }

    companion object {
        const val NAME: String = "Island"
    }
}

