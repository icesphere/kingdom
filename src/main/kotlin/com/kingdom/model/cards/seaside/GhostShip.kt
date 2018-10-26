package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class GhostShip : SeasideCard(NAME, CardType.ActionAttack, 5), AttackCard, ChooseCardActionCard {

    init {
        addCards = 2
        special = "Each other player with 4 or more cards in hand puts cards from their hand onto their deck until they have 3 cards in hand."
        fontSize = 11
        textSize = 100
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            if (opponent.hand.size >= 4) {
                opponent.addCardFromHandToTopOfDeck(chooseCardActionCard = this)
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        if (player.hand.size >= 4) {
            player.addCardFromHandToTopOfDeck(chooseCardActionCard = this)
        }
    }

    companion object {
        const val NAME: String = "Ghost Ship"
    }
}

