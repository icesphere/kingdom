package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForCardsInHand
import com.kingdom.model.cards.listeners.AfterOtherPlayerCardGainedListenerForCardsInHand
import com.kingdom.model.players.Player

class Pirate : SeasideCard(NAME, CardType.ActionDurationReaction, 5), ChoiceActionCard, StartOfTurnDurationAction,
        AfterCardGainedListenerForCardsInHand, AfterOtherPlayerCardGainedListenerForCardsInHand {

    init {
        special = "When any player gains a Treasure, you may play this from your hand. At the start of your next turn, gain a Treasure to your hand costing up to \$6."
        fontSize = 10
    }

    override fun afterCardGained(card: Card, player: Player) {
        maybePlayPirate(card, player)
    }

    override fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        maybePlayPirate(card, player)
    }

    private fun maybePlayPirate(card: Card, player: Player) {
        if (card.isTreasure && player.hand.contains(this)) {
            player.yesNoChoice(this, "Play ${cardNameWithBackgroundColor} from your hand?", card)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && player.hand.contains(this)) {
            player.removeCardFromHand(this)
            player.durationCards.add(this)
            player.cardRemovedFromPlay(this, CardLocation.PlayArea)
            player.addEventLogWithUsername("played ${cardNameWithBackgroundColor} from hand")
            player.opponents.forEach { it.showInfoMessage("${player.username} played ${cardNameWithBackgroundColor}") }
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.chooseSupplyCardToGainWithMaxCost(6, { card -> card.isTreasure },
                "Gain a Treasure to your hand costing up to \$6", CardLocation.Hand)
    }

    companion object {
        const val NAME: String = "Pirate"
    }
}
