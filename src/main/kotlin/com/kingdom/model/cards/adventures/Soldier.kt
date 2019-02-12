package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Soldier : AdventuresCard(NAME, CardType.ActionAttackTraveller, 3), CardDiscardedFromPlayListener, ChoiceActionCard, AttackCard {

    init {
        addCoins = 2
        special = "+\$1 per other Attack you have in play. Each other player with 4 or more cards in hand discards a card. When you discard this from play, you may exchange it for a Fugitive. (This is not in the Supply.)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val numAttacksInPlay = player.inPlay.count { it != this && it.isAttack }
        player.addCoins(numAttacksInPlay)
        player.showInfoMessage("Gained +\$$numAttacksInPlay from ${this.cardNameWithBackgroundColor}")
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            if (opponent.hand.size >= 4) {
                opponent.discardCardFromHand()
            }
        }
    }

    override fun onCardDiscarded(player: Player) {
        val fugitive = Fugitive()

        if (player.game.isCardAvailableInSupply(fugitive)) {
            player.yesNoChoice(this, "Exchange ${this.cardNameWithBackgroundColor} for a ${fugitive.cardNameWithBackgroundColor}?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.exchangeDiscardedCard(this, Fugitive())
        }
    }

    companion object {
        const val NAME: String = "Soldier"
    }
}

