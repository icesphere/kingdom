package com.kingdom.model.cards.actions

import com.kingdom.model.Player
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation


interface CardActionCard {
    fun isCardActionable(card: Card, cardAction: CardAction, cardLocation: CardLocation, player: Player): Boolean

    fun processCardAction(player: Player): Boolean

    fun processCardActionResult(cardAction: CardAction, player: Player, result: ActionResult)

    val isShowDoNotUse: Boolean
}