package com.kingdom.model.cards.menagerie

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.players.Player

class Fisherman : MenagerieCard(NAME, CardType.Action, 5), GameSetupModifier, CardCostModifier {

    init {
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "During your turns, if your discard pile is empty, this costs \$3 less"
    }

    override fun modifyGameSetup(game: Game) {
        game.gameCardCostModifiers.add(this)
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        if (player.isYourTurn && card.name == this.name && player.cardsInDiscard.isEmpty()) {
            return -3
        }

        return 0
    }

    companion object {
        const val NAME: String = "Fisherman"
    }
}

