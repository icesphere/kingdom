package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Bandit : KingdomCard(NAME, CardType.ActionAttack, 5), AttackCard, ChooseCardActionCard {

    init {
        testing = true
        special = "Gain a Gold. Each other player reveals the top two cards of their deck, trashes a revealed Treasure other than Copper, and discards the rest."
        textSize = 117
    }

    var cardsThatCanBeTrashed: MutableList<Card>? = null

    override fun cardPlayedSpecialAction(player: Player) {
        player.acquireFreeCardFromSupply(Gold())

        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {

        affectedOpponents
                .forEach { opponent ->
                    val topCardsOfDeck = opponent.revealTopCardsOfDeck(2)
                    val cardsThatCanBeTrashed = topCardsOfDeck.filter { it.isTreasure && !it.isCopper }
                    val cardsToDiscard = topCardsOfDeck.filter { !it.isTreasure || it.isCopper }
                    cardsToDiscard.forEach {
                        opponent.removeCardFromDeck(it)
                        opponent.addCardToDiscard(it)
                    }
                    if (cardsThatCanBeTrashed.isNotEmpty()) {
                        if (cardsThatCanBeTrashed.size == 1) {
                            val card = cardsThatCanBeTrashed.first()
                            opponent.removeCardFromDeck(card)
                            opponent.cardTrashed(card)
                            opponent.addGameLog("${this.cardNameWithBackgroundColor} trashed ${opponent.username}'s ${card.cardNameWithBackgroundColor}")
                        } else {
                            this.cardsThatCanBeTrashed = cardsThatCanBeTrashed.toMutableList()
                            opponent.chooseCardAction("Select a treasure to trash from the top of your deck. The other treasure will be discarded.", this, cardsThatCanBeTrashed, false)
                        }
                    }
                }
    }

    override fun onCardChosen(player: Player, card: Card) {
        player.addGameLog("${this.cardNameWithBackgroundColor} trashed ${player.username}'s ${card.cardNameWithBackgroundColor}")
        player.removeCardFromDeck(card)
        player.cardTrashed(card)
        cardsThatCanBeTrashed?.remove(card)
        val remainingCard = cardsThatCanBeTrashed?.first()!!
        player.removeCardFromDeck(remainingCard)
        player.addCardToDiscard(remainingCard)
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        cardsThatCanBeTrashed = null
    }

    companion object {
        const val NAME: String = "Bandit"
    }
}

