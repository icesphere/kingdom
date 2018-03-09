package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer

interface ChoiceActionCard {

    val name: String

    fun actionChoiceMade(player: OldPlayer, choice: Int)

}