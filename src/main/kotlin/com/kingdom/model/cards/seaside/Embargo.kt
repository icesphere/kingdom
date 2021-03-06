package com.kingdom.model.cards.seaside

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Embargo : SeasideCard(NAME, CardType.Action, 2), GameSetupModifier, ChooseCardActionCard {

    init {
        addCoins = 2
        special = "Trash this. Add an Embargo token to a Supply pile. (For the rest of the game, when a player buys a card from that pile, they gain a Curse.)"
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowEmbargoTokens = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardInPlay(this)
        player.chooseCardFromSupply("Add Embargo token to a card in the supply", this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        var numEmbargoTokens = player.game.embargoTokens[card.name] ?: 0
        numEmbargoTokens++
        player.game.embargoTokens[card.name] = numEmbargoTokens
    }

    companion object {
        const val NAME: String = "Embargo"
    }
}

