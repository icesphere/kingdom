package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.ConditionalDuration
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.actions.SetAsideCardsDuration
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.listeners.AfterOtherPlayerCardGainedListenerForCardsInPlay
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Blockade : SeasideCard(NAME, CardType.ActionDuration, 4), ChooseCardActionCard, ConditionalDuration,
        StartOfTurnDurationAction, SetAsideCardsDuration, AfterOtherPlayerCardGainedListenerForCardsInPlay {

    private var setAsideCard: Card? = null

    override val isKeepAtEndOfTurn: Boolean
        get() = setAsideCard != null

    override val setAsideCards: List<Card>?
        get() = setAsideCard?.let { listOf(it) } ?: emptyList()

    init {
        special = "Gain a card costing up to \$4, setting it aside. At the start of your next turn, put it into your hand. While it’s set aside, when another player gains a copy of it on their turn, they gain a Curse."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Gain a card costing up to \$4, setting it aside", this,
                { card -> card.debtCost == 0 && player.getCardCostWithModifiers(card) <= 4 },
                choosingEmptyPilesAllowed = false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.nextCardGainedSetAsideAction = { gainedCard ->
            setAsideCard = gainedCard
            player.addEventLogWithUsername("set aside ${gainedCard.cardNameWithBackgroundColor} with ${cardNameWithBackgroundColor}")
        }
        player.gainSupplyCard(card, showLog = true, destination = CardLocation.SetAside)
    }

    override fun durationStartOfTurnAction(player: Player) {
        setAsideCard?.let {
            player.addCardToHand(it)
            player.showInfoMessage("$cardNameWithBackgroundColor added ${it.cardNameWithBackgroundColor} to your hand")
        }
        setAsideCard = null
    }

    override fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        val blockadedCard = setAsideCard ?: return
        if (otherPlayer.isYourTurn && card.name == blockadedCard.name && otherPlayer.game.isCardAvailableInSupply(Curse())) {
            otherPlayer.gainSupplyCard(Curse(), showLog = true)
            otherPlayer.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor caused you to gain a ${Curse().cardNameWithBackgroundColor}")
            player.showInfoMessage("${otherPlayer.username} gained a ${Curse().cardNameWithBackgroundColor} from $cardNameWithBackgroundColor")
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        setAsideCard = null
    }

    companion object {
        const val NAME: String = "Blockade"
    }
}
