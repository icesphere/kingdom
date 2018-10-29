package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Contraband : ProsperityCard(NAME, CardType.Treasure, 5), ChooseCardActionCard {

    init {
        testing = true
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        addCoins = 3
        special = "When you play this, the player to your left names a card. You can’t buy that card this turn."
        fontSize = 11
        textSize = 81
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.game.previousPlayer!!.chooseCardAction("Chose a card that ${player.username} can't buy this turn", this, player.game.availableCards, false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.game.currentPlayer.cardsUnavailableToBuyThisTurn.add(card)
    }

    companion object {
        const val NAME: String = "Contraband"
    }
}

