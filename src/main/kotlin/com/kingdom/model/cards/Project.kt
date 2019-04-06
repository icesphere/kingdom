package com.kingdom.model.cards

import com.kingdom.model.players.Player

abstract class Project(name: String, deck: Deck, cost: Int) : Card(name, deck, CardType.Project, cost, 0) {

    open fun isProjectActionable(player: Player): Boolean {
        return player.buys > 0 && player.debt == 0 && player.availableCoins >= this.cost && player.projectsBought.size < 2 && (player.projectsBought.none { it.name == this.name })
    }

}