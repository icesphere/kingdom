package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.Curse
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object KingdomSpecialActionHandler {
    fun handleSpecialAction(game: OldGame, card: Card, repeatedAction: Boolean): IncompleteCard? {
        val supplyMap = game.supplyMap
        val players = game.players
        val currentPlayerId = game.currentPlayerId
        val currentPlayerIndex = game.currentPlayerIndex
        var incompleteCard: IncompleteCard? = null

        when (card.name) {
            "Adventurer" -> {
                val player = game.currentPlayer
                var treasureCardsFound = 0
                val setAsideCards = ArrayList<Card>()
                while (treasureCardsFound < 2) {
                    val revealedCard = player!!.removeTopDeckCard() ?: break
                    game.addHistory(player.username, " revealed ", KingdomUtil.getArticleWithCardName(revealedCard))
                    if (revealedCard.isTreasure) {
                        player.addCardToHand(revealedCard)
                        game.addHistory(player.username, " got a ", revealedCard.name, " from the ", KingdomUtil.getWordWithBackgroundColor("Adventurer", Card.ACTION_COLOR), " action")
                        treasureCardsFound++
                    } else {
                        setAsideCards.add(revealedCard)
                    }
                }
                player!!.discard.addAll(setAsideCards)
                for (c in setAsideCards) {
                    game.playerDiscardedCard(player, c)
                }
                game.refreshHand(player)
            }
            "Artisan" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
                cardAction.deck = Deck.Kingdom
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select one of the following cards to gain to your hand and then click Done."
                for (c in supplyMap.values) {
                    if (game.getCardCost(c) <= 5 && !c.costIncludesPotion && game.isCardInSupply(c)) {
                        cardAction.cards.add(c)
                    }
                }
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
            "Bureaucrat" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (player in game.players) {
                    if (player.userId != currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                        } else if (!player.hasMoat() && player.hasVictoryCard() && !player.hasLighthouse()) {
                            val victoryCards = KingdomUtil.uniqueCardList(player.getVictoryCards())
                            if (victoryCards.size == 1) {
                                incompleteCard.setPlayerActionCompleted(player.userId)
                                player.putCardFromHandOnTopOfDeck(player.getVictoryCards()[0])
                                game.refreshHand(player)
                                game.addHistory(player.username, " added 1 Victory card on top of ", player.pronoun, " deck")
                            } else {
                                val cardAction = OldCardAction(OldCardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK)
                                cardAction.deck = Deck.Kingdom
                                cardAction.cardName = card.name
                                cardAction.cards = victoryCards
                                cardAction.numCards = 1
                                cardAction.instructions = "Select a card to be placed on top of your deck and then click Done."
                                cardAction.buttonValue = "Done"
                                game.setPlayerCardAction(player, cardAction)
                            }
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            if (player.hasLighthouse()) {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                            } else if (player.hasMoat()) {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                            } else {
                                game.addHistory(player.username, " did not have a Victory card")
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
                val player = game.currentPlayer
                game.playerGainedCardToTopOfDeck(player!!, game.silverCard)
            }
            "Cellar" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_DISCARD_UP_TO_FROM_HAND)
                    cardAction.deck = Deck.Kingdom
                    cardAction.cardName = card.name
                    cardAction.cards.addAll(player.hand)
                    cardAction.numCards = player.hand.size
                    cardAction.instructions = "Discard any number of cards. +1 Card per card discarded. Select the Cards you want to discard and then click Done."
                    cardAction.buttonValue = "Done"
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Chancellor" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                cardAction.deck = Deck.Kingdom
                cardAction.cardName = card.name
                cardAction.instructions = "Would you like to put your deck into your discard pile?"
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Chapel" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND)
                    cardAction.deck = Deck.Kingdom
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 4
                    cardAction.instructions = "Trash up to 4 cards."
                    cardAction.cards = player.hand
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Council Room" -> for (player in players) {
                if (player.userId != currentPlayerId) {
                    player.drawCardAndAddToHand()
                    game.refreshHand(player)
                }
            }
            "Feast" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
                cardAction.deck = Deck.Kingdom
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select one of the following cards and then click Done."
                for (c in supplyMap.values) {
                    if (game.getCardCost(c) <= 5 && !c.costIncludesPotion && game.isCardInSupply(c)) {
                        cardAction.cards.add(c)
                    }
                }
                if (!repeatedAction) {
                    game.removePlayedCard(card)
                    game.trashedCards.add(card)
                    game.playerLostCard(player!!, card)
                    game.addHistory(KingdomUtil.getCardWithBackgroundColor(card), " was trashed")
                }
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
            "Library" -> {
                val player = game.currentPlayer
                while (player!!.hand.size < 7) {
                    val topCard = player.removeTopDeckCard() ?: break
                    if (topCard.isAction) {
                        val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                        cardAction.deck = Deck.Kingdom
                        cardAction.cardName = card.name
                        cardAction.cards.add(topCard)
                        cardAction.instructions = "Do you want to set aside this action card?"
                        game.setPlayerCardAction(player, cardAction)
                        break
                    } else {
                        player.addCardToHand(topCard)
                    }
                }
                game.refreshHand(player)
            }
            "Militia" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (player in players) {
                    if (player.userId != currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                        } else if (!player.hasMoat() && player.hand.size > 3 && !player.hasLighthouse()) {
                            val cardAction = OldCardAction(OldCardAction.TYPE_DISCARD_DOWN_TO_FROM_HAND)
                            cardAction.deck = Deck.Kingdom
                            cardAction.cardName = card.name
                            cardAction.cards.addAll(player.hand)
                            cardAction.numCards = 3
                            cardAction.instructions = "Discard down to 3 cards. Select the Cards you want to discard and then click Done."
                            cardAction.buttonValue = "Done"
                            game.setPlayerCardAction(player, cardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            if (player.hasLighthouse()) {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                            } else if (player.hasMoat()) {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                            } else {
                                game.addHistory(player.username, " had 3 or less cards")
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Mine" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                cardAction.deck = Deck.Kingdom
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select a treasure card to trash."
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.treasureCards)
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("You don't have any treasure cards to trash."))
                }
            }
            "Moneylender" -> {
                val player = game.currentPlayer
                for (handCard in player!!.hand) {
                    if (handCard.name == "Copper") {
                        player.removeCardFromHand(handCard)
                        game.trashedCards.add(handCard)
                        game.playerLostCard(player, handCard)
                        player.addCoins(3)
                        game.addHistory(player.username, " trashed ", KingdomUtil.getArticleWithCardName(handCard), " and gained 3 coins")
                        game.refreshHand(player)
                        game.refreshDiscard(player)
                        game.refreshCardsBought(player)
                        break
                    }
                }
            }
            "Remodel" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Kingdom
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Spy" -> {
                incompleteCard = SinglePlayerIncompleteCard(card.name, game)
                val currentPlayer = game.currentPlayer!!
                if (currentPlayer.lookAtTopDeckCard() != null) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Kingdom
                    cardAction.cardName = card.name
                    cardAction.instructions = "You are spying the top card of your deck. Do you want to discard it?"
                    cardAction.cards.add(currentPlayer.lookAtTopDeckCard()!!)
                    cardAction.playerId = currentPlayer.userId
                    incompleteCard.extraOldCardActions.add(cardAction)
                } else {
                    game.addHistory(currentPlayer.username, " did not have a card to draw")
                }
                var nextPlayerIndex = game.nextPlayerIndex
                while (nextPlayerIndex != currentPlayerIndex) {
                    val nextPlayer = players[nextPlayerIndex]
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                        game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        if (nextPlayer.lookAtTopDeckCard() != null) {
                            val nextCardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                            nextCardAction.deck = Deck.Kingdom
                            nextCardAction.cardName = card.name
                            nextCardAction.instructions = "You are spying the top card of " + nextPlayer.username + "'s deck. Do you want to discard it?"
                            nextCardAction.cards.add(nextPlayer.lookAtTopDeckCard()!!)
                            nextCardAction.playerId = nextPlayer.userId
                            incompleteCard.extraOldCardActions.add(nextCardAction)
                        } else {
                            game.addHistory(nextPlayer.username, " did not have a card to draw")
                        }
                    } else {
                        if (nextPlayer.hasLighthouse()) {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                        } else {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                        }
                    }
                    if (nextPlayerIndex == players.size - 1) {
                        nextPlayerIndex = 0
                    } else {
                        nextPlayerIndex++
                    }
                }
                if (!incompleteCard.extraOldCardActions.isEmpty()) {
                    val cardAction = incompleteCard.extraOldCardActions.remove()
                    game.setPlayerCardAction(currentPlayer, cardAction)
                }
            }
            "Thief" -> {
                incompleteCard = SinglePlayerIncompleteCard(card.name, game)
                val currentPlayer = game.currentPlayer
                var nextPlayerIndex = game.nextPlayerIndex
                while (nextPlayerIndex != currentPlayerIndex) {
                    val nextPlayer = players[nextPlayerIndex]
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                        game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        val nextCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                        nextCardAction.deck = Deck.Kingdom
                        nextCardAction.playerId = nextPlayer.userId
                        nextCardAction.cardName = card.name
                        val card1 = nextPlayer.removeTopDeckCard()
                        val card2 = nextPlayer.removeTopDeckCard()
                        var instructions: String
                        if (card1 != null) {
                            instructions = "These are the top two cards from " + nextPlayer.username + "'s deck."
                            if (!card1.isTreasure) {
                                card1.isDisableSelect = true
                            }
                            nextCardAction.cards.add(card1)
                            if (card2 != null) {
                                if (!card2.isTreasure) {
                                    card2.isDisableSelect = true
                                }
                                nextCardAction.cards.add(card2)
                            }
                        } else {
                            instructions = nextPlayer.username + " did not have any cards to draw."
                        }
                        if (card1 != null && card1.isTreasure || card2 != null && card2.isTreasure) {
                            instructions += " Select a treasure card to trash and then click Done."
                            nextCardAction.buttonValue = "Done"
                            nextCardAction.numCards = 1
                        } else {
                            instructions += " There are no treasure cards to trash. Click Continue."
                            nextCardAction.buttonValue = "Continue"
                            nextCardAction.numCards = 0
                        }
                        nextCardAction.instructions = instructions
                        incompleteCard.extraOldCardActions.add(nextCardAction)
                    } else {
                        if (nextPlayer.hasLighthouse()) {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                        } else {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                        }
                    }
                    if (nextPlayerIndex == players.size - 1) {
                        nextPlayerIndex = 0
                    } else {
                        nextPlayerIndex++
                    }
                }
                if (!incompleteCard.extraOldCardActions.isEmpty()) {
                    val cardAction = incompleteCard.extraOldCardActions.remove()
                    game.setPlayerCardAction(currentPlayer!!, cardAction)
                }
            }
            "Throne Room" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Kingdom
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select one of the following actions to play twice and then click Done."
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.actionCards)
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Witch" -> {
                var nextPlayerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                while (nextPlayerIndex != game.currentPlayerIndex) {
                    val nextPlayer = players[nextPlayerIndex]
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                        game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        if (game.isCardInSupply(Curse.NAME)) {
                            game.playerGainedCard(nextPlayer, game.curseCard)
                            game.refreshDiscard(nextPlayer)
                        }
                    } else {
                        if (nextPlayer.hasLighthouse()) {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                        } else {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                        }
                    }
                    nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex)
                }
            }
            "Workshop" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
                cardAction.deck = Deck.Kingdom
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select one of the following cards to gain and then click Done."
                for (c in supplyMap.values) {
                    if (game.getCardCost(c) <= 4 && !c.costIncludesPotion && game.isCardInSupply(c)) {
                        cardAction.cards.add(c)
                    }
                }
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
        }

        return incompleteCard
    }
}