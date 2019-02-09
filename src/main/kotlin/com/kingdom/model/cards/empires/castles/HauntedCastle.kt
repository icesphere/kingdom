package com.kingdom.model.cards.empires.castles

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.empires.EmpiresCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class HauntedCastle : EmpiresCard(NAME, CardType.VictoryCastle, 6), AfterCardGainedListenerForSelf, ChooseCardActionCard {

    init {
        victoryPoints = 2
        special = "When you gain this during your turn, gain a Gold, and each other player with 5 or more cards in hand puts 2 cards from their hand onto their deck."
        textSize = 71
        fontSize = 9
    }

    override fun afterCardGained(player: Player) {
        if (player.isYourTurn) {
            player.gainSupplyCard(Gold(), true)

            for (opponent in player.opponentsInOrder) {
                if (opponent.hand.size >= 5) {
                    opponent.addCardFromHandToTopOfDeck(chooseCardActionCard = this)
                    opponent.addCardFromHandToTopOfDeck(chooseCardActionCard = this)
                }
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        //do nothing
    }

    override val pileName: String
        get() = Castles.NAME

    companion object {
        const val NAME: String = "Haunted Castle"
    }
}

