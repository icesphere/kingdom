package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class Rabble : ProsperityCard(NAME, CardType.ActionAttack, 5), AttackCard {

    init {
        testing = true
        addCards = 3
        special = "Each other player reveals the top 3 cards of their deck, discards the Actions and Treasures, and puts the rest back in any order they choose."
        textSize = 80
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            val cards = opponent.removeTopCardsOfDeck(3, true)

            val actionsAndTreasures = cards.filter { it.isAction || it.isTreasure }

            actionsAndTreasures.forEach { opponent.addCardToDiscard(it, showLog = true) }

            val otherCards = cards.filter { !it.isAction && !it.isTreasure }

            if (otherCards.size > 1) {
                opponent.putCardsOnTopOfDeckInAnyOrder(otherCards)
            } else {
                otherCards.forEach { opponent.addCardToTopOfDeck(it, true) }
            }
        }
    }

    companion object {
        const val NAME: String = "Rabble"
    }
}

