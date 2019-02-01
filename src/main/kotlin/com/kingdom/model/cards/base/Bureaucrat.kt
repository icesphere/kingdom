package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

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

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {

        affectedOpponents
                .forEach { opponent ->
                    val victoryCards = opponent.hand.filter { it.isVictory }
                    if (victoryCards.isNotEmpty()) {
                        if (victoryCards.size == 1) {
                            val victoryCard = victoryCards[0]
                            opponent.revealCardFromHand(victoryCard)
                            opponent.removeCardFromHand(victoryCard)
                            opponent.addCardToTopOfDeck(victoryCard)
                            player.showInfoMessage("${opponent.username} put ${victoryCard.cardNameWithBackgroundColor} on top of their deck")
                            opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor put ${victoryCard.cardNameWithBackgroundColor} on top of your deck")
                        } else {
                            player.showInfoMessage("${opponent.username} is choosing a victory card from ${victoryCards.groupedString} to put on top of their deck")
                            opponent.addCardFromHandToTopOfDeck({ c -> c.isVictory })
                        }
                    } else {
                        player.showInfoMessage("${opponent.username} did not have any victory cards in their hand: ${opponent.hand.groupedString}")
                        opponent.revealHand()
                    }
                }
    }

    companion object {
        const val NAME: String = "Bureaucrat"
    }
}

