package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Gear : AdventuresCard(NAME, CardType.ActionDuration, 3), StartOfTurnDurationAction, ChooseCardsActionCard {

    var setAsideCards: List<Card>? = null

    init {
        addCards = 2
        special = "Set aside up to 2 cards from your hand face down (under this). At the start of your next turn, put them into your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardsFromHand(special, 2, true, this)
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        setAsideCards = cards
        if (cards.isNotEmpty()) {
            cards.forEach { player.removeCardFromHand(it) }
            player.addEventLogWithUsername("set aside cards under ${this.cardNameWithBackgroundColor}")
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        if (setAsideCards?.isNotEmpty() == true) {
            player.addCardsToHand(setAsideCards!!)
            player.showInfoMessage("${this.cardNameWithBackgroundColor} added ${setAsideCards!!.groupedString} to your hand")
            player.addEventLogWithUsername("put cards from ${this.cardNameWithBackgroundColor} into their hand")
        }

        setAsideCards = null
    }

    companion object {
        const val NAME: String = "Gear"
    }
}

