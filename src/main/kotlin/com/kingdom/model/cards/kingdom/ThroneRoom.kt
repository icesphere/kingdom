package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ActionResult
import com.kingdom.model.cards.actions.CardAction
import com.kingdom.model.cards.actions.CardActionCard
import com.kingdom.model.players.Player

class ThroneRoom : KingdomCard(NAME, CardType.Action, 4), CardActionCard {

    var copiedCard: Card? = null

    init {
        special = "You may play an Action card from your hand twice."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCardAction(this, "Choose an action card from your hand to play twice")
    }

    companion object {
        const val NAME: String = "Throne Room"
    }

    override fun isCardActionable(card: Card, cardAction: CardAction, cardLocation: CardLocation, player: Player): Boolean {
        return card.isAction && cardLocation == CardLocation.Hand
    }

    override fun processCardAction(player: Player): Boolean {
        return player.hand.any { it.isAction }
    }

    override fun processCardActionResult(cardAction: CardAction, player: Player, result: ActionResult) {
        result.selectedCard?.let {
            copiedCard = it
            player.actions += 1
            player.playCard(it)
            player.addRepeatCardAction(it)
        }
    }

    override fun removedFromPlay(player: Player) {
        copiedCard = null
    }

    override val isShowDoNotUse: Boolean = true
}

