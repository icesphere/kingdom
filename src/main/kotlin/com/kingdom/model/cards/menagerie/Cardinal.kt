package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Cardinal : MenagerieCard(NAME, CardType.ActionAttack, 4), UsesExileMat, AttackCard, ChooseCardActionCard {

    init {
        addCoins = 2
        special = "Each other player reveals the top 2 cards of their deck, Exiles one costing from \$3 to \$6, and discards the rest."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {

        for (opponent in affectedOpponents) {
            val topCardsOfDeck = opponent.removeTopCardsOfDeck(2, true)
            player.showInfoMessage("${opponent.username} revealed ${topCardsOfDeck.groupedString}")

            val cardsThatCanBeExiled = topCardsOfDeck.filter { player.getCardCostWithModifiers(it) in 3..6 }
            val cardsToDiscard = topCardsOfDeck - cardsThatCanBeExiled

            opponent.addCardsToDiscard(cardsToDiscard, true)

            when (cardsThatCanBeExiled.size) {
                1 -> {
                    val card = cardsThatCanBeExiled.first()
                    if (player.getCardCostWithModifiers(card) in 3..6) {
                        opponent.exileCard(card)
                    } else {
                        opponent.addCardToDiscard(card, showLog = true)
                    }
                }
                2 -> {
                    opponent.chooseCardAction("Select a card to exile. The other card will be discarded.", this, cardsThatCanBeExiled, false, cardsThatCanBeExiled.toMutableList())
                }
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        @Suppress("UNCHECKED_CAST")
        val cardsThatCanBeExiled = info as MutableList<Card>
        player.exileCard(card)
        cardsThatCanBeExiled.remove(card)
        player.addCardToDiscard(cardsThatCanBeExiled.first(), showLog = true)
    }

    companion object {
        const val NAME: String = "Cardinal"
    }
}

