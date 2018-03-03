package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.util.KingdomUtil
import java.util.*

object TrashCardsHandler {
    fun handleCardAction(game: Game, player: Player, cardAction: CardAction, selectedCardIds: List<Int>): IncompleteCard? {

        var incompleteCard: IncompleteCard? = null

        val cardMap = game.cardMap
        val supplyMap = game.supplyMap

        if (selectedCardIds.isEmpty()) {
            game.addHistory(player.username, " did not trash a card")
            return null
        }

        val cardsTrashed = ArrayList<Card>()
        for (selectedCardId in selectedCardIds) {
            val cardToTrash = player.getCardFromHandById(selectedCardId)
            player.removeCardFromHand(cardToTrash!!)
            game.trashedCards.add(cardToTrash)
            game.playerLostCard(player, cardToTrash)
            cardsTrashed.add(cardToTrash)
        }
        if (!cardsTrashed.isEmpty()) {
            game.addHistory(player.username, " trashed ", KingdomUtil.groupCards(cardsTrashed, true))
        }

        val trashedCard = cardMap[selectedCardIds[0]]!!

        when (cardAction.cardName) {
            "Alms" -> {
                player.addSins(-1)
                game.refreshAllPlayersPlayers()
                for (otherPlayer in game.players) {
                    if (otherPlayer.userId != game.currentPlayerId) {
                        game.playerGainedCardToHand(otherPlayer, game.copperCard)
                        game.refreshHand(otherPlayer)
                    }
                }
            }
            "Apprentice" -> {
                val numCardsToDraw = game.getCardCost(trashedCard)
                player.drawCards(numCardsToDraw)
                if (trashedCard.costIncludesPotion) {
                    player.drawCards(2)
                }
            }
            "Assassin" -> {
                game.currentPlayer!!.addSins(2)
                game.refreshAllPlayersPlayers()
                game.refreshHandArea(game.currentPlayer!!)
                game.addHistory(game.currentPlayer!!.username, " gained 2 sins")
            }
            "Bishop" -> {
                val victoryCoinsGained = Math.floor((game.getCardCost(trashedCard) / 2).toDouble()).toInt()
                game.addHistory(player.username, " gained ", KingdomUtil.getPlural(victoryCoinsGained, "Victory Coin"), " from the ", KingdomUtil.getWordWithBackgroundColor("Bishop", Card.ACTION_COLOR), " card")
                player.addVictoryCoins(victoryCoinsGained)
                game.refreshAllPlayersPlayers()

                incompleteCard = MultiPlayerIncompleteCard(cardAction.cardName, game, false)
                for (otherPlayer in game.players) {
                    if (otherPlayer.userId != game.currentPlayerId) {
                        if (otherPlayer.hand.size > 0) {
                            val trashCardAction = CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND).apply {
                                deck = Deck.Prosperity
                                cardName = "Bishop 2"
                                cards = KingdomUtil.uniqueCardList(otherPlayer.hand)
                                numCards = 1
                                instructions = "Select a card to trash and then click Done, or just click Done if you don't want to trash a card."
                                buttonValue = "Done"
                            }
                            game.setPlayerCardAction(otherPlayer, trashCardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(otherPlayer.userId)
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Develop" -> {

                var cost = game.getCardCost(trashedCard)
                cost += 1

                val cardsMore = supplyMap.values
                        .filterTo(ArrayList()) {
                            game.getCardCost(it) == cost && trashedCard.costIncludesPotion == it.costIncludesPotion && game.isCardInSupply(it)
                        }

                cost -= 2

                val cardsLess = supplyMap.values
                        .filterTo(ArrayList()) {
                            game.getCardCost(it) == cost && trashedCard.costIncludesPotion == it.costIncludesPotion && game.isCardInSupply(it)
                        }

                if (!cardsLess.isEmpty() && !cardsMore.isEmpty()) {
                    val chooseWhichCardAction = CardAction(CardAction.TYPE_CHOICES).apply {
                        deck = Deck.Hinterlands
                        cardName = cardAction.cardName
                        instructions = "Which do you want to do first: Gain a card costing $1 more than the trashed card, or Gain a card costing $1 less than the trashed card?"
                        choices.add(CardActionChoice("$1 More", "more"))
                        choices.add(CardActionChoice("$1 Less", "less"))
                        cards.add(trashedCard)
                        associatedCard = trashedCard
                    }
                    game.setPlayerCardAction(player, chooseWhichCardAction)
                } else {
                    val gainCardAction = CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                        deck = Deck.Hinterlands
                        cardName = cardAction.cardName
                        buttonValue = "Done"
                        numCards = 1
                        associatedCard = trashedCard
                        phase = 3
                        instructions = "Select one of the following cards to gain and then click Done."
                    }
                    when {
                        cardsLess.isNotEmpty() -> {
                            gainCardAction.cards = cardsLess
                            game.setPlayerCardAction(player, gainCardAction)
                        }
                        cardsMore.isNotEmpty() -> {
                            gainCardAction.cards = cardsMore
                            game.setPlayerCardAction(player, gainCardAction)
                        }
                        else -> game.addHistory("There were no cards that cost $1 more and no cards that cost $1 less than ", KingdomUtil.getCardWithBackgroundColor(trashedCard))
                    }
                }
            }
            "Expand" -> {
                val secondCardAction = CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                    deck = Deck.Prosperity
                    cardName = "Expand"
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards and then click Done."
                }
                val highestCost = game.getCardCost(trashedCard) + 3

                supplyMap.values
                        .filterTo(secondCardAction.cards) {
                            game.getCardCost(it) <= highestCost && (trashedCard.costIncludesPotion || !it.costIncludesPotion) && game.isCardInSupply(it)
                        }

                if (secondCardAction.cards.isNotEmpty()) {
                    game.setPlayerCardAction(player, secondCardAction)
                }
            }
            "Farmland" -> {
                val secondCardAction = CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                    deck = Deck.Hinterlands
                    cardName = cardAction.cardName
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards and then click Done."
                    isGainCardAfterBuyAction = cardAction.isGainCardAfterBuyAction
                    associatedCard = cardAction.associatedCard
                }
                var cost = game.getCardCost(trashedCard)
                cost += 2

                supplyMap.values
                        .filterTo(secondCardAction.cards) {
                            game.getCardCost(it) == cost && trashedCard.costIncludesPotion == it.costIncludesPotion && game.isCardInSupply(it)
                        }

                if (secondCardAction.cards.size == 1) {
                    game.playerGainedCard(player, secondCardAction.cards[0])
                } else if (!secondCardAction.cards.isEmpty()) {
                    cardAction.isGainCardAfterBuyAction = false
                    game.setPlayerCardAction(player, secondCardAction)
                }
            }
            "Forge" -> when {
                selectedCardIds.isNotEmpty() -> {
                    val cost = selectedCardIds
                            .mapNotNull { cardMap[it] }
                            .sumBy { game.getCardCost(it) }

                    val secondCardAction = CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                        deck = Deck.Prosperity
                        cardName = "Forge"
                        buttonValue = "Done"
                        numCards = 1
                        instructions = "Select one of the following cards and then click Done."
                    }

                    supplyMap.values
                            .filterTo(secondCardAction.cards) {
                                game.getCardCost(it) == cost && !it.costIncludesPotion && game.isCardInSupply(it)
                            }

                    when {
                        secondCardAction.cards.size > 0 -> game.setPlayerCardAction(player, secondCardAction)
                        else -> game.addHistory("There were no cards that had a cost equal to the cost of the cards trashed")
                    }
                }
                else -> game.addHistory(player.username, " chose to not trash any cards")
            }
            "Governor" -> {
                val secondCardAction = CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                    deck = Deck.Promo
                    cardName = cardAction.cardName
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards to gain and then click Done."
                }
                var cost = game.getCardCost(trashedCard)
                cost += if (game.isCurrentPlayer(player)) {
                    2
                } else {
                    1
                }
                supplyMap.values
                        .filterTo(secondCardAction.cards) {
                            game.getCardCost(it) == cost && trashedCard.costIncludesPotion == it.costIncludesPotion && game.isCardInSupply(it)
                        }

                if (secondCardAction.cards.isNotEmpty()) {
                    game.setPlayerCardAction(player, secondCardAction)
                }
            }
            "Mine" -> {
                val secondCardAction = CardAction(CardAction.TYPE_GAIN_CARDS_INTO_HAND_FROM_SUPPLY).apply {
                    deck = Deck.Kingdom
                    cardName = "Mine"
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards to put into your hand and then click Done."
                }
                game.availableTreasureCardsInSupply
                        .filterTo(secondCardAction.cards) {
                            game.getCardCost(trashedCard) + 3 >= game.getCardCost(it) && (trashedCard.costIncludesPotion || !it.costIncludesPotion)
                        }

                if (secondCardAction.cards.isNotEmpty()) {
                    game.setPlayerCardAction(player, secondCardAction)
                } else {
                    game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("There were no treasure cards to gain."))
                }
            }
            "Remake" -> {
                val secondCardAction = CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                    deck = Deck.Cornucopia
                    cardName = cardAction.cardName
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards and then click Done."
                }
                var cost = game.getCardCost(trashedCard)
                cost += 1

                supplyMap.values
                        .filterTo(secondCardAction.cards) {
                            game.getCardCost(it) == cost && trashedCard.costIncludesPotion == it.costIncludesPotion && game.isCardInSupply(it)
                        }

                if (secondCardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, secondCardAction)
                }
            }
            "Remodel" -> {
                val secondCardAction = CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                    deck = Deck.Kingdom
                    cardName = "Remodel"
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards and then click Done."
                }

                val highestCost = game.getCardCost(trashedCard) + 2

                supplyMap.values
                        .filterTo(secondCardAction.cards) {
                            game.getCardCost(it) <= highestCost && (trashedCard.costIncludesPotion || !it.costIncludesPotion) && game.isCardInSupply(it)
                        }

                if (secondCardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, secondCardAction)
                }
            }
            "Salvager" -> player.addCoins(game.getCardCost(trashedCard))
            "Sorceress" -> {
                val cursesRemaining = game.supply[Card.CURSE_ID]!!
                if (cursesRemaining > 0 || cardAction.phase == 1 && cardAction.choices.size > 1) {
                    val nextCardAction = CardAction(CardAction.TYPE_CHOICES)
                    nextCardAction.deck = Deck.FairyTale
                    nextCardAction.cardName = cardAction.cardName

                    if (cursesRemaining == 0) {
                        nextCardAction.instructions = "There are no curses remaining so you may only choose one more effect."
                    } else {
                        nextCardAction.instructions = "Choose another effect to apply (you will gain a curse), or click None if you don't want to apply any more effects."
                    }

                    nextCardAction.choices.addAll(cardAction.choices)
                    nextCardAction.phase = cardAction.phase + 1
                    game.setPlayerCardAction(player, nextCardAction)
                }
            }
            "Spice Merchant" -> if (!selectedCardIds.isEmpty()) {
                val choicesAction = CardAction(CardAction.TYPE_CHOICES).apply {
                    deck = Deck.Hinterlands
                    cardName = cardAction.cardName
                    instructions = "Choose one: +2 Cards and +1 Action; or +$2 and +1 Buy."
                    choices.add(CardActionChoice("+2 Cards and +1 Action", "cards"))
                    choices.add(CardActionChoice("+$2 and +1 Buy", "money"))
                }
                game.setPlayerCardAction(player, choicesAction)
            }
            "Trader" -> {
                var numSilversToGain = game.getCardCost(trashedCard)
                while (game.isCardInSupply(Card.SILVER_ID) && numSilversToGain > 0) {
                    game.playerGainedCard(player, game.silverCard)
                    numSilversToGain--
                }
            }
            "Trading Post" -> if (game.isCardInSupply(Card.SILVER_ID)) {
                game.playerGainedCardToHand(player, game.silverCard)
            } else {
                game.addHistory("There were no more ", KingdomUtil.getCardWithBackgroundColor(game.silverCard), " cards in the supply")
            }
            "Transmute" -> {
                if (trashedCard.isAction) {
                    if (game.isCardInSupply(Card.DUCHY_ID)) {
                        game.playerGainedCard(player, game.duchyCard)
                    }
                }
                if (trashedCard.isTreasure) {
                    if (game.isCardInSupply(cardAction.cardId)) {
                        game.playerGainedCard(player, cardMap[cardAction.cardId]!!)
                    }
                }
                if (trashedCard.isVictory) {
                    if (game.isCardInSupply(Card.GOLD_ID)) {
                        game.playerGainedCard(player, game.goldCard)
                    }
                }
            }
            "Upgrade" -> {
                val secondCardAction = CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                    deck = Deck.Intrigue
                    cardName = "Upgrade"
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards and then click Done."
                }
                var cost = game.getCardCost(trashedCard)
                cost += 1

                supplyMap.values
                        .filterTo(secondCardAction.cards) {
                            game.getCardCost(it) == cost && trashedCard.costIncludesPotion == it.costIncludesPotion && game.isCardInSupply(it)
                        }

                if (secondCardAction.cards.size == 1) {
                    game.playerGainedCard(player, secondCardAction.cards[0])
                } else if (!secondCardAction.cards.isEmpty()) {
                    game.setPlayerCardAction(player, secondCardAction)
                }
            }
        }

        return incompleteCard
    }
}
