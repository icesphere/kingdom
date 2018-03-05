package com.kingdom.model.cards.actions

import com.kingdom.model.Player

interface ChoiceActionCard {

    val name: String

    fun actionChoiceMade(player: Player, choice: Int)

}