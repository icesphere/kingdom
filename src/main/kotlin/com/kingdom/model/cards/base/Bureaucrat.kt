package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Bureaucrat : BaseCard(NAME, CardType.ActionAttack, 4), AttackCard {
    init {
        special = "Gain a silver card; put it on top of your deck. Each other player reveals a Victory card from his hand and puts it on his deck (or reveals a hand with no Victory cards)."
        fontSize = 11
        textSize = 121
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.isNextCardToTopOfDeck = true
        player.gainSupplyCard(Silver())
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {

        affectedOpponents
                .forEach { opponent ->
                    val victoryCards = opponent.hand.filter { it.isVictory }
                    if (victoryCards.isNotEmpty()) {
                        if (victoryCards.size == 1) {
                            val victoryCard = victoryCards[0]
                            opponent.revealCardFromHand(victoryCard)
                            opponent.removeCardFromHand(victoryCard)
                            opponent.addCardToTopOfDeck(victoryCard)
                        } else {
                            opponent.addCardFromHandToTopOfDeck({ c -> c.isVictory })
                        }
                    } else {
                        opponent.revealHand()
                    }
                }
    }

    companion object {
        const val NAME: String = "Bureaucrat"
    }
}

