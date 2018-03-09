package com.kingdom.model.cards.actions

import com.kingdom.model.OldPlayer
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class CardAction(private val cardActionCard: CardActionCard, text: String?) : Action(text) {

    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: OldPlayer): Boolean {
        return cardActionCard.isCardActionable(card, this, cardLocation, player);
    }

    override fun processAction(player: OldPlayer): Boolean {
        return cardActionCard.processCardAction(player);
    }

    override fun processActionResult(player: OldPlayer, result: ActionResult): Boolean {
        cardActionCard.processCardActionResult(this, player, result);
        return true;
    }

    override var isShowDoNotUse: Boolean = cardActionCard.isShowDoNotUse

}