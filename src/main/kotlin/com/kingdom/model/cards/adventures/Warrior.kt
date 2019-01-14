package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Warrior : AdventuresCard(NAME, CardType.ActionAttackTraveller, 4), CardDiscardedFromPlayListener, ChoiceActionCard, AttackCard {

    init {
        addCards = 2
        special = "Once per Traveller you have in play (including this), each other player discards the top card of their deck and trashes it if it costs \$3 or \$4. When you discard this from play, you may exchange it for a Hero. (This is not in the Supply.)"
        textSize = 102
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        val numTravellersInPlay = player.inPlayWithDuration.count { it.isTraveller }
        for (opponent in affectedOpponents) {
            repeat(numTravellersInPlay) {
                val topCardOfDeck = opponent.discardTopCardOfDeck()
                opponent.showInfoMessage("${player.username}'s ${this.cardNameWithBackgroundColor} discarded $topCardOfDeck from the top of your deck")
                if (topCardOfDeck != null) {
                    val cost = player.getCardCostWithModifiers(topCardOfDeck)
                    if (cost == 3 || cost == 4) {
                        opponent.showInfoMessage("${player.username}'s ${this.cardNameWithBackgroundColor} trashed $topCardOfDeck")
                        opponent.trashCardFromDiscard(topCardOfDeck)
                    }
                }
            }
        }
    }

    override fun onCardDiscarded(player: Player) {
        val hero = Hero()

        if (player.game.isCardAvailableInSupply(hero)) {
            player.yesNoChoice(this, "Exchange ${this.cardNameWithBackgroundColor} for a ${hero.cardNameWithBackgroundColor}?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.exchangeDiscardedCard(this, Hero())
        }
    }

    companion object {
        const val NAME: String = "Warrior"
    }
}

