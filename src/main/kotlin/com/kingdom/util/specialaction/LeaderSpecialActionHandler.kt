package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.Curse

object LeaderSpecialActionHandler {

    fun handleSpecialAction(game: Game, card: Card) {
        val player = game.currentPlayer

        when (card.name) {
            "Archimedes" -> {
                player!!.buyBonusTurns = 2
                player.setEnableVictoryCardDiscount(true)
            }
            "Aristotle" -> player!!.cardBonusTurns = 2
            "Bilkis" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Leaders
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select one of the following cards to gain on top of your deck and then click Done."
                for (c in game.supplyMap.values) {
                    if (game.getCardCost(c) <= 6 && !c.costIncludesPotion && game.supply[c.name]!! > 0) {
                        cardAction.cards.add(c)
                    }
                }
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
            "Imhotep" -> {
                player!!.buyBonusTurns = 2
                player.setEnableTreasureCardDiscount(true)
            }
            "Leonidas" -> {
                player!!.buyBonusTurns = 2
                player.setEnableActionCardDiscount(true)
            }
            "Maecenas" -> player!!.setLeaderDiscount(3)
            "Plato" -> if (player!!.hand.size > 0) {
                val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND)
                cardAction.deck = Deck.Leaders
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 2
                cardAction.instructions = "Trash up to 2 cards."
                cardAction.cards = player.hand
                game.setPlayerCardAction(player, cardAction)
            }
            "Tomyris" -> {
                var nextPlayerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                while (nextPlayerIndex != game.currentPlayerIndex) {
                    val nextPlayer = game.players[nextPlayerIndex]
                    val numInSupply = game.supply[Curse.NAME]!!
                    if (numInSupply > 0) {
                        game.playerGainedCard(nextPlayer, game.curseCard)
                        game.refreshDiscard(nextPlayer)
                    }
                    nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex)
                }
            }
            "Xenophon" -> {
                player!!.addCoins(2)
                player.addBuys(1)
                game.refreshAllPlayersPlayingArea()
            }
        }
    }
}
