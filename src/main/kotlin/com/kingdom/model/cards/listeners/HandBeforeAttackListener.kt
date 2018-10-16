package com.kingdom.model.cards.listeners

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface HandBeforeAttackListener {

    fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player)
}