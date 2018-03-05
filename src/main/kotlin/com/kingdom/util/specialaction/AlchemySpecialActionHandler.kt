package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object AlchemySpecialActionHandler {
    fun handleSpecialAction(game: Game, card: Card): IncompleteCard? {

        val supplyMap = game.supplyMap
        val players = game.players
        val currentPlayerId = game.currentPlayerId
        val currentPlayerIndex = game.currentPlayerIndex
        var incompleteCard: IncompleteCard? = null

        when (card.name) {
            "Apothecary" -> {
                val player = game.currentPlayer
                val cards = ArrayList<Card>()
                var hasMoreCards = true
                var cardsRevealed = 0
                val revealedCards = ArrayList<Card>()
                while (hasMoreCards && cardsRevealed < 4) {
                    val c = player!!.removeTopDeckCard()
                    if (c == null) {
                        hasMoreCards = false
                    } else {
                        cardsRevealed++
                        revealedCards.add(c)
                        if (c.isCopper || c.isPotion) {
                            player.addCardToHand(c)
                        } else {
                            cards.add(c)
                        }
                    }
                }
                if (revealedCards.size > 0) {
                    game.addHistory("Apothecary revealed ", KingdomUtil.groupCards(revealedCards, true))
                } else {
                    game.addHistory(player!!.username, " did not have any cards to reveal")
                }
                if (cards.size == 1) {
                    player!!.addCardToTopOfDeck(cards[0])
                } else if (cards.size > 0) {
                    game.refreshHand(player!!)
                    val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER)
                    cardAction.deck = Deck.Alchemy
                    cardAction.isHideOnSelect = true
                    cardAction.numCards = cards.size
                    cardAction.cardName = card.name
                    cardAction.cards = cards
                    cardAction.buttonValue = "Done"
                    cardAction.instructions = "Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)"
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Apprentice" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Alchemy
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.addHistory(player.username, " did not have any cards")
                }
            }
            "Familiar" -> for (player in players) {
                if (player.userId != currentPlayerId) {
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                        game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                    } else if (!player.hasMoat() && !player.hasLighthouse()) {
                        val topCard = player.removeTopDeckCard()
                        if (topCard != null) {
                            player.addCardToDiscard(topCard)
                            game.playerDiscardedCard(player, topCard)
                            if (game.isCardInSupply(Card.CURSE_ID)) {
                                game.playerGainedCard(player, game.curseCard)
                                game.refreshDiscard(player)
                            }
                        }
                    } else {
                        if (player.hasLighthouse()) {
                            game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                        } else {
                            game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                        }
                    }
                }
            }
            "Golem" -> {
                val player = game.currentPlayer
                val revealedCards = ArrayList<Card>()
                val setAsideCards = ArrayList<Card>()
                val cards = ArrayList<Card>()
                var hasMoreCards = true
                var actionCardsFound = 0
                while (hasMoreCards && actionCardsFound < 2) {
                    val c = player!!.removeTopDeckCard()
                    if (c == null) {
                        hasMoreCards = false
                    } else {
                        revealedCards.add(c)
                        if (c.isAction && c.name != "Golem") {
                            actionCardsFound++
                            cards.add(c)
                        } else {
                            setAsideCards.add(c)
                        }
                    }
                }
                if (revealedCards.size > 0) {
                    game.addHistory(player!!.username, "'s ", KingdomUtil.getCardWithBackgroundColor(card), " revealed ", KingdomUtil.groupCards(revealedCards, true))
                    player.discard.addAll(setAsideCards)
                    for (c in setAsideCards) {
                        game.playerDiscardedCard(player, c)
                    }
                }
                if (cards.size == 1) {
                    game.golemActions.push(cards[0])
                } else if (cards.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                    cardAction.deck = Deck.Alchemy
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.cards = cards
                    cardAction.instructions = "Select which action you would like to play first and then click Done."
                    game.setPlayerCardAction(player!!, cardAction)
                } else {
                    game.addHistory("No actions were found for the ", KingdomUtil.getCardWithBackgroundColor(card), " to play.")
                }
            }
            "Scrying Pool" -> {
                incompleteCard = SinglePlayerIncompleteCard(card.name, game)
                val currentPlayer = game.currentPlayer!!
                if (currentPlayer.lookAtTopDeckCard() != null) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Alchemy
                    cardAction.cardName = card.name
                    cardAction.instructions = "You are looking at the top card of your deck. Do you want to discard it?"
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
                            nextCardAction.deck = Deck.Alchemy
                            nextCardAction.cardName = card.name
                            nextCardAction.instructions = "You are looking at the top card of " + nextPlayer.username + "'s deck. Do you want to discard it?"
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
                } else {
                    var foundNonActionCard = false
                    while (!foundNonActionCard) {
                        val topDeckCard = currentPlayer.removeTopDeckCard() ?: break
                        game.addHistory(currentPlayer.username, " revealed ", KingdomUtil.getArticleWithCardName(topDeckCard))
                        if (!topDeckCard.isAction) {
                            foundNonActionCard = true
                        }
                        currentPlayer.addCardToHand(topDeckCard)
                    }
                }
            }
            "Transmute" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Alchemy
                    cardAction.cardName = card.name
                    cardAction.cardId = card.cardId
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.addHistory(player.username, " did not have any cards")
                }
            }
            "University" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_GAIN_UP_TO_FROM_SUPPLY)
                cardAction.deck = Deck.Alchemy
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select one of the following cards to gain and then click Done, or just click Done if you don't want to gain a card."
                supplyMap.values
                        .filterTo (cardAction.cards) { it.isAction && game.getCardCost(it) <= 5 && !it.costIncludesPotion && game.isCardInSupply(it) }
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
        }

        return incompleteCard
    }
}