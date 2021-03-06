package com.kingdom.model.cards.base

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Bandit : BaseCard(NAME, CardType.ActionAttack, 5), AttackCard, ChooseCardActionCard {

    init {
        special = "Gain a Gold. Each other player reveals the top two cards of their deck, trashes a revealed Treasure other than Copper, and discards the rest."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Gold())

        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {

        affectedOpponents
                .forEach { opponent ->
                    val topCardsOfDeck = opponent.revealTopCardsOfDeck(2)
                    val cardsThatCanBeTrashed = topCardsOfDeck.filter { it.isTreasure && !it.isCopper }
                    val cardsToDiscard = topCardsOfDeck.filter { !it.isTreasure || it.isCopper }
                    cardsToDiscard.forEach {
                        opponent.removeCardFromDeck(it)
                        opponent.addCardToDiscard(it)
                    }
                    if (cardsToDiscard.isNotEmpty()) {
                        opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor discarded ${cardsToDiscard.groupedString} from your deck")
                    }
                    if (cardsThatCanBeTrashed.isNotEmpty()) {
                        when {
                            cardsThatCanBeTrashed.size == 1 -> {
                                val card = cardsThatCanBeTrashed.first()
                                opponent.removeCardFromDeck(card)
                                opponent.cardTrashed(card)
                                opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor trashed ${card.cardNameWithBackgroundColor} from your deck")
                                opponent.addEventLog("${this.cardNameWithBackgroundColor} trashed ${opponent.username}'s ${card.cardNameWithBackgroundColor}")
                            }
                            cardsThatCanBeTrashed[0].name == cardsThatCanBeTrashed[1].name -> {
                                opponent.removeCardFromDeck(cardsThatCanBeTrashed[0])
                                opponent.cardTrashed(cardsThatCanBeTrashed[0])
                                opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor trashed ${cardsThatCanBeTrashed[0].cardNameWithBackgroundColor} from your deck")
                                opponent.addEventLog("${this.cardNameWithBackgroundColor} trashed ${opponent.username}'s ${cardsThatCanBeTrashed[0].cardNameWithBackgroundColor}")

                                opponent.removeCardFromDeck(cardsThatCanBeTrashed[1])
                                opponent.addCardToDiscard(cardsThatCanBeTrashed[1])
                                opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor discarded ${cardsThatCanBeTrashed[1].cardNameWithBackgroundColor} from your deck")
                            }
                            else -> {
                                opponent.chooseCardAction("Select a treasure to trash from the top of your deck. The other treasure will be discarded.", this, cardsThatCanBeTrashed, false, cardsThatCanBeTrashed.toMutableList())
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
        const val NAME: String = "Bandit"
    }
}

