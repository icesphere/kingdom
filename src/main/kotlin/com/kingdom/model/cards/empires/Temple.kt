package com.kingdom.model.cards.empires

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Temple : EmpiresCard(NAME, CardType.ActionGathering, 4), AfterCardGainedListenerForSelf, ChooseCardsActionCard {

    init {
        addVictoryCoins = 1
        special = "Trash from 1 to 3 differently named cards from your hand. Add 1 VP to the Temple Supply pile. When you gain this, take the VP from the Temple Supply pile."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            selectCardsToTrash(player)
        }

        player.game.addVictoryPointToSupplyPile(pileName)
    }

    private fun selectCardsToTrash(player: Player) {
        player.chooseCardsFromHand("Trash from 1 to 3 differently named cards from your hand.", 3, true, this, allowDoNotUse = false)
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        val groupedCards = cards.groupBy { it.name }

        when {
            groupedCards.isEmpty() -> {
                player.showInfoMessage("You have to trash at least 1 card")
                selectCardsToTrash(player)
            }
            groupedCards.size != cards.size -> {
                player.showInfoMessage("Cards must be differently named")
                selectCardsToTrash(player)
            }
            else -> cards.forEach { player.trashCardFromHand(it) }
        }
    }

    override fun afterCardGained(player: Player) {
        val victoryPointsOnSupplyPile = player.game.victoryPointsOnSupplyPile[pileName] ?: 0
        if (victoryPointsOnSupplyPile > 0) {
            player.takeAllVictoryPointsFromSupplyPile(this)
        }
    }

    companion object {
        const val NAME: String = "Temple"
    }
}

