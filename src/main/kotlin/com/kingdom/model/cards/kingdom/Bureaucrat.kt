package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Bureaucrat : KingdomCard(NAME, CardType.ActionAttack, 4) {
    init {
        special = "Gain a silver card; put it on top of your deck. Each other player reveals a Victory card from his hand and puts it on his deck (or reveals a hand with no Victory cards)."
        fontSize = 11
        textSize = 94
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.isNextCardToTopOfDeck = true
        player.acquireFreeCardFromSupply(Silver())

        var addWaitingAction = false

        player.opponents.forEach {
            val victoryCards = it.hand.filter { it.isVictory }
            if (victoryCards.isNotEmpty()) {
                if (victoryCards.size == 1) {
                    val victoryCard = victoryCards[0]
                    it.revealCardFromHand(victoryCard)
                    it.hand.remove(victoryCard)
                    it.addCardToTopOfDeck(victoryCard)
                } else {
                    it.addCardFromHandToTopOfDeck({ c -> c.isVictory })
                    addWaitingAction = true
                }
            } else {
                it.revealHand()
            }
        }

        if (addWaitingAction) {
            player.waitForOtherPlayersToResolveActions()
        }
    }

    companion object {
        const val NAME: String = "Bureaucrat"
    }
}

