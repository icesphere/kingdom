package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Contraband : ProsperityCard(NAME, CardType.Treasure, 5), ChooseCardActionCard {

    init {
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        addCoins = 3
        special = "When you play this, the player to your left names a card. You can’t buy that card this turn."
        fontSize = 11
        textSize = 81
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.playerToLeft.chooseCardAction("Chose a card that ${player.username} can't buy this turn", this, player.game.availableCardsCopy.sortedBy { it.cost }, false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.game.currentPlayer.cardsUnavailableToBuyThisTurn.add(card)
        player.game.currentPlayer.addEventLogWithUsername("can't buy ${card.cardNameWithBackgroundColor} this turn")
        player.game.currentPlayer.showInfoMessage("You can't buy ${card.cardNameWithBackgroundColor} this turn")
    }

    companion object {
        const val NAME: String = "Contraband"
    }
}

