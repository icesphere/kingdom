package com.kingdom.model.cards.prosperity

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.players.Player

class Peddler : ProsperityCard(NAME, CardType.Action, 8), GameSetupModifier, CardCostModifier {

    init {
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "During your Buy phase, this costs \$2 less per Action card you have in play, but not less than \$0."
        textSize = 57
    }

    override fun modifyGameSetup(game: Game) {
        game.gameCardCostModifiers.add(this)
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        if (card.name == this.name) {
            return player.inPlay.count { it.isAction } * -2
        }

        return 0
    }

    companion object {
        const val NAME: String = "Peddler"
    }
}

