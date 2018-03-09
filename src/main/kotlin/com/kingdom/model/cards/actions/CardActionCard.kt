package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation


interface CardActionCard {
    fun isCardActionable(card: Card, cardAction: CardAction, cardLocation: CardLocation, player: OldPlayer): Boolean

    fun processCardAction(player: OldPlayer): Boolean

    fun processCardActionResult(cardAction: CardAction, player: OldPlayer, result: ActionResult)

    val isShowDoNotUse: Boolean
}