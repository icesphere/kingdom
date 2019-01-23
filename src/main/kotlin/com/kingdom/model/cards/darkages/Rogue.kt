package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Rogue : DarkAgesCard(NAME, CardType.ActionAttack, 5), AttackCard, ChooseCardActionCard {

    init {
        addCoins = 2
        special = "If there are any cards in the trash costing from \$3 to \$6, gain one of them. Otherwise, each other player reveals the top two cards of their deck, trashes one of them costing from \$3 to \$6, and discards the rest."
        textSize = 92
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.game.trashedCards.any { player.getCardCostWithModifiers(it) in 3..6 }) {
            player.gainCardFromTrash(false, { c -> player.getCardCostWithModifiers(c) in 3..6 })
        } else {
            player.triggerAttack(this)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            val topCardsOfDeck = opponent.revealTopCardsOfDeck(2)
            player.showInfoMessage("${opponent.username} revealed ${topCardsOfDeck.groupedString}")

            val cardsThatCanBeTrashed = topCardsOfDeck.filter { player.getCardCostWithModifiers(it) in 3..6 }
            val cardsToDiscard = topCardsOfDeck - cardsThatCanBeTrashed

            cardsToDiscard.forEach {
                opponent.removeCardFromDeck(it)
                opponent.addCardToDiscard(it)
            }

            if (cardsToDiscard.isNotEmpty()) {
                opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor discarded ${cardsToDiscard.groupedString} from your deck")
                player.showInfoMessage("Discarded ${opponent.username}'s ${cardsToDiscard.groupedString}")
            }

            if (cardsThatCanBeTrashed.isNotEmpty()) {
                when {
                    cardsThatCanBeTrashed.size == 1 -> {
                        val card = cardsThatCanBeTrashed.first()
                        opponent.removeCardFromDeck(card)
                        opponent.cardTrashed(card)
                        opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor trashed ${card.cardNameWithBackgroundColor} from your deck")
                        player.showInfoMessage("Trashed ${opponent.username}'s ${card.cardNameWithBackgroundColor}")
                        opponent.addEventLog("${this.cardNameWithBackgroundColor} trashed ${opponent.username}'s ${card.cardNameWithBackgroundColor}")
                    }
                    cardsThatCanBeTrashed[0].name == cardsThatCanBeTrashed[1].name -> {
                        opponent.removeCardFromDeck(cardsThatCanBeTrashed[0])
                        opponent.cardTrashed(cardsThatCanBeTrashed[0])
                        opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor trashed ${cardsThatCanBeTrashed[0].cardNameWithBackgroundColor} from your deck")
                        player.showInfoMessage("Trashed ${opponent.username}'s ${cardsThatCanBeTrashed[0].cardNameWithBackgroundColor}")
                        opponent.addEventLog("${this.cardNameWithBackgroundColor} trashed ${opponent.username}'s ${cardsThatCanBeTrashed[0].cardNameWithBackgroundColor}")

                        opponent.removeCardFromDeck(cardsThatCanBeTrashed[1])
                        opponent.addCardToDiscard(cardsThatCanBeTrashed[1])
                    }
                    else -> {
                        opponent.chooseCardAction("Select a card to trash from the top of your deck. The other card will be discarded.", this, cardsThatCanBeTrashed, false, cardsThatCanBeTrashed.toMutableList())
                    }
                }
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        @Suppress("UNCHECKED_CAST")
        val cardsThatCanBeTrashed = info as MutableList<Card>

        player.addEventLog("${this.cardNameWithBackgroundColor} trashed ${player.username}'s ${card.cardNameWithBackgroundColor}")
        player.removeCardFromDeck(card)
        player.cardTrashed(card)
        cardsThatCanBeTrashed.remove(card)
        val remainingCard = cardsThatCanBeTrashed.first()
        player.removeCardFromDeck(remainingCard)
        player.addCardToDiscard(remainingCard)
    }

    companion object {
        const val NAME: String = "Rogue"
    }
}

