package com.kingdom.model.cards.empires.events

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Tax : EmpiresEvent(NAME, 2), GameSetupModifier, ChooseCardActionCard {

    init {
        special = "Add 2 debt to a Supply pile. Setup: Add 1 debt to each Supply pile. When a player buys a card, they take the debt from its pile."
    }

    override fun modifyGameSetup(game: Game) {
        (game.cardsInSupply + game.kingdomCards).forEach { game.debtOnSupplyPile[it.pileName] = 1 }
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Add 2 debt to a Supply pile", this)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.game.addDebtToSupplyPile(card.pileName, 2)
    }

    companion object {
        const val NAME: String = "Tax"
    }
}