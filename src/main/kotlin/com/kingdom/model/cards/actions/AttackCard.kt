package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player

interface AttackCard {

    fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?)
}