package com.kingdom.model.cards.risingsun

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Event
import com.kingdom.model.cards.Prophecy

abstract class RisingSunCard(
        name: String,
        type: CardType,
        cost: Int,
        debtCost: Int = 0,
        omen: Boolean = false,
        shadow: Boolean = false,
        command: Boolean = false
) : Card(name, Deck.RisingSun, type, cost, debtCost,
        additionalTypes = listOfNotNull(
                if (omen) "Omen" else null,
                if (shadow) "Shadow" else null,
                if (command) "Command" else null
        ).toSet()) {

    init {
        isOmen = omen
        isShadow = shadow
        isCommand = command
    }
}

abstract class RisingSunEvent(
        name: String,
        cost: Int,
        debtCost: Int = 0,
        oncePerTurnEvent: Boolean = false
) : Event(name, Deck.RisingSun, cost, oncePerTurnEvent, debtCost)

abstract class RisingSunProphecy(name: String) : Prophecy(name, Deck.RisingSun)
