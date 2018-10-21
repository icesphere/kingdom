package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Ambassador : SeasideCard(NAME, CardType.Action, 3), AttackCard {

    init {
        disabled = true
        special = "Reveal a card from your hand. Return up to 2 copies of it from your hand to the Supply. Then each other player gains a copy of it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        //todo
    }

    companion object {
        const val NAME: String = "Ambassador"
    }
}

