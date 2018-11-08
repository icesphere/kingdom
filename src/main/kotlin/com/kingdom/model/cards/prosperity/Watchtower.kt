package com.kingdom.model.cards.prosperity

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.CardGainedListenerForCardsInHand
import com.kingdom.model.players.Player

class Watchtower : ProsperityCard(NAME, CardType.ActionReaction, 3), CardGainedListenerForCardsInHand, ChoiceActionCard {

    var gainedCards = mutableListOf<Card>()

    init {
        special = "Draw until you have 6 cards in hand. When you gain a card, you may reveal this from your hand, to either trash that card or put it onto your deck."
        fontSize = 11
        textSize = 123
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.size < 6) {
            player.drawCards(6 - player.hand.size)
        }
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        gainedCards.add(card)
        player.game.currentPlayer.waitForOtherPlayersToResolveActions()
        player.yesNoChoice(this, "Reveal ${this.cardNameWithBackgroundColor} to trash ${card.cardNameWithBackgroundColor} or put ${card.cardNameWithBackgroundColor} on top of your deck?")
        return true
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = gainedCards.removeAt(0)

        when(choice) {
            1 -> {
                gainedCards.add(card)
                player.makeChoice(this, "${card.cardNameWithBackgroundColor} - Trash or put on top of deck?", Choice(3, "Trash"), Choice(4, "Top of deck"))
            }
            2 -> player.cardAcquired(card)
            3 -> player.cardTrashed(card, showLog = true)
            4 -> player.addCardToTopOfDeck(card)
        }
    }

    companion object {
        const val NAME: String = "Watchtower"
    }
}

