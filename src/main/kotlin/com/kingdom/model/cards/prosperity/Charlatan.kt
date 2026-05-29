package com.kingdom.model.cards.prosperity

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Charlatan : ProsperityCard(NAME, CardType.ActionAttack, 5), AttackCard, GameSetupModifier {

    init {
        addCoins = 3
        special = "Each other player gains a Curse. In games using this, Curse is also a Treasure worth \$1."
        isCurseGiver = true
    }

    override fun modifyGameSetup(game: Game) {
        game.isCurseTreasure = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { it.gainSupplyCard(Curse(), true) }
    }

    companion object {
        const val NAME: String = "Charlatan"
    }
}
