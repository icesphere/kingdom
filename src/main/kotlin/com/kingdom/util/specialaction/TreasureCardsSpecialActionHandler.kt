package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil
import java.util.*

object TreasureCardsSpecialActionHandler {
    fun handleSpecialAction(game: OldGame, card: Card): IncompleteCard? {

        var incompleteCard: IncompleteCard? = null
        val player = game.currentPlayer

        when (card.name) {
            "Contraband" -> {
                val nextPlayer = game.players[game.nextPlayerIndex]
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, nextPlayer.userId)
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Prosperity
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select a card that " + player!!.username + " can't buy this turn."
                cardAction.cards.addAll(game.supplyMap.values)
                game.setPlayerCardAction(nextPlayer, cardAction)
                incompleteCard.allActionsSet()
            }
            "Diadem" -> if (player!!.actions > 0) {
                player.addCoins(player.actions)
                game.addHistory(player.username, " gained ", KingdomUtil.getPlural(player.actions, "Coin"), " from ", KingdomUtil.getCardWithBackgroundColor(card))
            } else {
                game.addHistory(player.username, " did not have any unused actions")
            }
            "Fool's Gold" -> if (player!!.isFoolsGoldPlayed) {
                player.addCoins(4)
            } else {
                player.addCoins(1)
                player.isFoolsGoldPlayed = true
            }
            "Horn of Plenty" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
                cardAction.deck = Deck.Cornucopia
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select one of the following cards to gain and then click Done."
                cardAction.associatedCard = card
                val cardNames = game.cardsPlayed
                        .map { it.name }
                        .toSet()
                for (c in game.supplyMap.values) {
                    if (game.getCardCost(c) <= cardNames.size && !c.costIncludesPotion && game.supply[c.name]!! > 0) {
                        cardAction.cards.add(c)
                    }
                }
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
            "Ill-Gotten Gains" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                cardAction.deck = Deck.Hinterlands
                cardAction.cardName = "Ill-Gotten Gains"
                cardAction.instructions = "Do you want to gain a Copper card into your hand?"
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Loan" -> {
                var treasureCardFound = false
                val revealedCards = ArrayList<Card>()
                val setAsideCards = ArrayList<Card>()
                var treasureCard: Card? = null
                while (!treasureCardFound) {
                    val revealedCard = player!!.removeTopDeckCard() ?: break
                    revealedCards.add(revealedCard)
                    if (revealedCard.isTreasure) {
                        treasureCardFound = true
                        treasureCard = revealedCard
                    } else {
                        setAsideCards.add(revealedCard)
                    }
                }
                if (!revealedCards.isEmpty()) {
                    game.addHistory(player!!.username, " revealed ", KingdomUtil.groupCards(revealedCards, true))
                } else {
                    game.addHistory(player!!.username, " did not have any cards")
                }
                player.discard.addAll(setAsideCards)
                for (c in setAsideCards) {
                    game.playerDiscardedCard(player, c)
                }
                if (treasureCard != null) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                    cardAction.deck = Deck.Prosperity
                    cardAction.cardName = card.name
                    cardAction.cards.add(treasureCard)
                    cardAction.instructions = "Do you want to discard or trash this card?"
                    cardAction.choices.add(CardActionChoice("Discard", "discard"))
                    cardAction.choices.add(CardActionChoice("Trash", "trash"))
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Quarry" -> {
                game.incrementActionCardDiscount(2)
                game.refreshAllPlayersSupply()
                game.refreshAllPlayersPlayingArea()
                game.refreshAllPlayersHandArea()
            }
            "Venture" -> {
                var treasureCardFound = false
                val revealedCards = ArrayList<Card>()
                val setAsideCards = ArrayList<Card>()
                while (!treasureCardFound) {
                    val revealedCard = player!!.removeTopDeckCard() ?: break
                    revealedCards.add(revealedCard)
                    if (revealedCard.isTreasure) {
                        treasureCardFound = true
                        game.playTreasureCard(player, revealedCard, false, true)
                    } else {
                        setAsideCards.add(revealedCard)
                    }
                }
                if (!revealedCards.isEmpty()) {
                    game.addHistory(player!!.username, " revealed ", KingdomUtil.groupCards(revealedCards, true))
                } else {
                    game.addHistory(player!!.username, " did not have any cards")
                }
                player.discard.addAll(setAsideCards)
                for (c in setAsideCards) {
                    game.playerDiscardedCard(player, c)
                }
            }
        }

        return incompleteCard
    }
}
