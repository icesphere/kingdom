package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.modifiers.CardCostModifierForCardsInPlay
import com.kingdom.model.players.Player

class BridgeTroll : AdventuresCard(NAME, CardType.ActionAttackDuration, 5), StartOfTurnDurationAction, CardCostModifierForCardsInPlay, AttackCard {

    init {
        special = "Each other player takes their -\$1 token which will cause those players to get \$1 less the next time they get \$. Now and at the start of your next turn: +1 Buy. While this is in play, cards cost \$1 less on your turns, but not less than \$0."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
        player.addBuys(1)
        player.game.refreshSupply()
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addBuys(1)
        player.showInfoMessage("Gained +1 Buy from ${this.cardNameWithBackgroundColor}")
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            opponent.isMinusCoinTokenInFrontOfPlayer = true
            opponent.showInfoMessage("${player.username}'s ${this.cardNameWithBackgroundColor} gave you your -\$1 token")
            opponent.refreshPlayerHandArea()
        }
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        return -1
    }

    companion object {
        const val NAME: String = "Bridge Troll"
    }
}

