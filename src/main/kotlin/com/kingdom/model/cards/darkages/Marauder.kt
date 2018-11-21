package com.kingdom.model.cards.darkages

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class Marauder : DarkAgesCard(NAME, CardType.ActionAttackLooter, 4), GameSetupModifier, AttackCard {

    init {
        special = "Gain a Spoils from the Spoils pile. Each other player gains a Ruins."
    }

    override fun modifyGameSetup(game: Game) {
        game.isIncludeSpoils = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainRuins()
        player.gainSpoils()
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            opponent.gainRuins()
        }
    }

    companion object {
        const val NAME: String = "Marauder"
    }
}

