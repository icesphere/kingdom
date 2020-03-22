package com.kingdom.model.cards.menagerie

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.players.Player

class Destrier : MenagerieCard(NAME, CardType.Action, 6), GameSetupModifier, CardCostModifier {

    init {
        addCards = 2
        addActions = 1
        special = "During your turns, this costs \$1 less per card you’ve gained this turn."
    }

    override fun modifyGameSetup(game: Game) {
        game.gameCardCostModifiers.add(this)
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        if (player.isYourTurn && card.name == this.name) {
            return player.cardsGained.size * -1
        }

        return 0
    }

    companion object {
        const val NAME: String = "Destrier"
    }
}

