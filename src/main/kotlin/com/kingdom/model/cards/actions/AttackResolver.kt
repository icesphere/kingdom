package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player

interface AttackResolver {

    fun resolveAttack(player: Player, affectedOpponents: List<Player>)
}