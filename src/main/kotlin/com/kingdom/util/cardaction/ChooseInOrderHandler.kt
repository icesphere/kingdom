package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.util.KingdomUtil

object ChooseInOrderHandler {
    fun handleCardAction(game: Game, player: Player, cardAction: CardAction, selectedCardIds: List<Int>): IncompleteCard? {

        var incompleteCard: IncompleteCard? = null

        val cardMap = game.cardMap

        when (cardAction.cardName) {
            "Apothecary", "Navigator", "Scout", "Rabble", "Ghost Ship", "Mandarin", "Cartographer", "Oracle" -> {
                val cards = selectedCardIds.map { cardMap[it]!! }
                player.deck.addAll(0, cards)

                when (cardAction.cardName) {
                    "Ghost Ship" -> game.addHistory(player.username, " added ", KingdomUtil.getPlural(selectedCardIds.size, "card"), " on top of ", player.pronoun, " deck")
                    "Mandarin" -> {
                        game.addHistory(player.username, " added ", KingdomUtil.getPlural(selectedCardIds.size, "treasure card"), " from play on top of ", player.pronoun, " deck")
                        game.cardsPlayed.removeAll(game.treasureCardsPlayed)
                        game.treasureCardsPlayed.clear()
                        game.refreshAllPlayersCardsPlayed()
                    }
                    "Oracle" -> if ((!game.hasIncompleteCard() || game.incompleteCard!!.extraCardActions.isEmpty()) && game.isCurrentPlayer(player)) {
                        player.drawCards(2)
                    }
                }
            }
            "Black Market" -> {
                val cards = selectedCardIds.map { cardMap[it]!! }
                game.blackMarketCards.addAll(cards)
            }
            "Black Market Treasure" -> {
                var queueTreasureCards = false
                for (selectedCardId in selectedCardIds) {
                    val card = cardMap[selectedCardId]!!
                    if (!queueTreasureCards && card.isAutoPlayTreasure) {
                        game.playTreasureCard(player, card, true, true, false, true, true)
                    } else {
                        game.blackMarketTreasureQueue.add(card)
                        queueTreasureCards = true
                    }
                }
                incompleteCard = SinglePlayerIncompleteCard("Black Market", game)
                game.addNextAction("Buy Card")
                incompleteCard.setPlayerActionCompleted(player.userId)
            }
            "Lookout" -> {
                val cardToTrash = cardMap[selectedCardIds[0]]!!
                game.trashedCards.add(cardToTrash)
                game.playerLostCard(player, cardToTrash)
                game.addHistory("The ", KingdomUtil.getWordWithBackgroundColor("Lookout", Card.ACTION_COLOR), " trashed ", player.username, "'s ", cardToTrash.name)
                val cardToDiscard = cardMap[selectedCardIds[1]]!!
                player.addCardToDiscard(cardToDiscard)
                game.playerDiscardedCard(player, cardToDiscard)
                game.addHistory("The ", KingdomUtil.getWordWithBackgroundColor("Lookout", Card.ACTION_COLOR), " discarded ", player.username, "'s ", cardToDiscard.name)
                if (selectedCardIds.size == 3) {
                    val cardToPutBack = cardMap[selectedCardIds[2]]!!
                    player.addCardToTopOfDeck(cardToPutBack)
                }
            }
        }

        return incompleteCard
    }
}
