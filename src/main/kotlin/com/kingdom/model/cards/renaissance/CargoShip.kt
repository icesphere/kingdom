package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.SetAsideCardsDuration
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForCardsInPlay
import com.kingdom.model.cards.listeners.TurnEndedListenerForCardsPlayedThisTurn
import com.kingdom.model.players.Player

class CargoShip : RenaissanceCard(NAME, CardType.ActionDuration, 3), StartOfTurnDurationAction, ChoiceActionCard, SetAsideCardsDuration, AfterCardGainedListenerForCardsInPlay, TurnEndedListenerForCardsPlayedThisTurn {

    var setAsideCard: Card? = null

    var thisTurn = false

    override val setAsideCards: List<Card>?
        get() = setAsideCard?.let { listOf(it) } ?: emptyList()

    init {
        addCoins = 2
        special = "Once this turn, when you gain a card, you may set it aside face up (on this). At the start of your next turn, put it into your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        thisTurn = true
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (thisTurn && setAsideCard == null) {
            player.yesNoChoice(this, "Set aside ${card.cardNameWithBackgroundColor} with $cardNameWithBackgroundColor?", card)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val card = info as Card
            player.addEventLogWithUsername("set aside ${card.cardNameWithBackgroundColor} with $cardNameWithBackgroundColor")
            player.removeCard(card)
            setAsideCard = card
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        setAsideCard?.let {
            player.addCardToHand(it)
            player.showInfoMessage("$cardNameWithBackgroundColor added ${it.cardNameWithBackgroundColor} to your hand")
        }

        setAsideCard = null
    }

    override fun onTurnEnded(player: Player) {
        thisTurn = false
    }

    companion object {
        const val NAME: String = "Cargo Ship"
    }
}

