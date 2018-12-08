package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.HandBeforeAttackListener
import com.kingdom.model.players.Player

class CaravanGuard : AdventuresCard(NAME, CardType.ActionDurationReaction, 3), StartOfTurnDurationAction, HandBeforeAttackListener, ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        special = "At the start of your next turn, +\$1. When another player plays an Attack card, you may first play this from your hand. (+1 Action has no effect if it’s not your turn.)"
        fontSize = 10
        textSize = 82
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addCoins(1)
    }

    override fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player) {
        player.yesNoChoice(this, "Play ${this.cardNameWithBackgroundColor}?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.removeCardFromHand(this)
            player.drawCard()
            player.durationCards.add(this)
        }
    }

    companion object {
        const val NAME: String = "Caravan Guard"
    }
}

