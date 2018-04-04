package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Bandit : KingdomCard(NAME, CardType.ActionAttack, 5) {

    init {
        special = "Gain a Gold. Each other player reveals the top two cards of their deck, trashes a revealed Treasure other than Copper, and discards the rest."
        textSize = 117
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.acquireFreeCardFromSupply(Gold())

        var addWaitingAction = false

        player.opponents.forEach {
            val topCardsOfDeck = it.revealTopCardsOfDeck(2)
            val cardsThatCanBeTrashed = topCardsOfDeck.filter { it.isTreasure && !it.isCopper }
            if (cardsThatCanBeTrashed.isNotEmpty()) {
                if (cardsThatCanBeTrashed.size == 1) {
                    val card = cardsThatCanBeTrashed.first()
                    it.removeCardFromDeck(card)
                    it.cardTrashed(card)
                    it.addGameLog("${this.cardNameWithBackgroundColor} trashed ${it.username}'s ${card.cardNameWithBackgroundColor}")
                } else {
                    it.selectCardsToTrashFromDeck(cardsThatCanBeTrashed, 1, false)
                    addWaitingAction = true
                }
            }
        }

        if (addWaitingAction) {
            player.waitForOtherPlayersToResolveActions()
        }
    }

    companion object {
        const val NAME: String = "Bandit"
    }
}

