package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.util.KingdomUtil

import java.util.ArrayList
import java.util.HashSet

object CornucopiaSpecialActionHandler {
    fun handleSpecialAction(game: Game, card: Card): IncompleteCard? {

        val players = game.players
        val player = game.currentPlayer
        var incompleteCard: IncompleteCard? = null

        when (card.name) {
            "Bag of Gold" -> when {
                game.supply[Card.GOLD_ID]!! > 0 -> game.playerGainedCardToTopOfDeck(player!!, game.goldCard)
                else -> game.addHistory("The supply did not have any ", KingdomUtil.getCardWithBackgroundColor(game.goldCard))
            }
            "Farming Village" -> {
                val revealedCards = ArrayList<Card>()
                val setAsideCards = ArrayList<Card>()
                var topDeckCard: Card? = null
                var foundActionOrTreasure = false
                while (!foundActionOrTreasure) {
                    topDeckCard = player!!.removeTopDeckCard()
                    if (topDeckCard == null) {
                        break
                    }
                    revealedCards.add(topDeckCard)
                    if (topDeckCard.isAction || topDeckCard.isTreasure) {
                        foundActionOrTreasure = true
                        player.addCardToHand(topDeckCard)
                    } else {
                        setAsideCards.add(topDeckCard)
                    }
                }
                if (!revealedCards.isEmpty()) {
                    game.addHistory(player!!.username, " revealed ", KingdomUtil.groupCards(revealedCards, true))
                    player.discard.addAll(setAsideCards)
                    for (c in setAsideCards) {
                        game.playerDiscardedCard(player, c)
                    }
                    if (foundActionOrTreasure) {
                        game.addHistory(player.username, " added ", KingdomUtil.getArticleWithCardName(topDeckCard!!), " to ", player.pronoun, " hand")
                    }
                } else {
                    game.addHistory(player!!.username, " did not have any cards to draw")
                }
            }
            "Followers" -> {
                if (game.supply[Card.ESTATE_ID]!! > 0) {
                    game.playerGainedCard(player!!, game.estateCard)
                }
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                var nextPlayerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                while (nextPlayerIndex != game.currentPlayerIndex) {
                    val nextPlayer = players[nextPlayerIndex]
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                        incompleteCard.setPlayerActionCompleted(nextPlayer.userId)
                        game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        val cursesInSupply = game.supply[Card.CURSE_ID]!!
                        if (cursesInSupply > 0) {
                            game.playerGainedCard(nextPlayer, game.curseCard)
                            game.refreshDiscard(nextPlayer)
                        }
                        if (nextPlayer.hand.size > 3) {
                            val cardAction = CardAction(CardAction.TYPE_DISCARD_DOWN_TO_FROM_HAND)
                            cardAction.deck = Deck.Cornucopia
                            cardAction.cardName = card.name
                            cardAction.cards.addAll(nextPlayer.hand)
                            cardAction.numCards = 3
                            cardAction.instructions = "Discard down to 3 cards. Select the Cards you want to discard and then click Done."
                            cardAction.buttonValue = "Done"
                            game.setPlayerCardAction(nextPlayer, cardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(nextPlayer.userId)
                            game.addHistory(nextPlayer.username, " had 3 or less cards")
                        }
                    } else {
                        incompleteCard.setPlayerActionCompleted(nextPlayer.userId)
                        if (nextPlayer.hasLighthouse()) {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                        } else if (nextPlayer.hasMoat()) {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                        }
                    }
                    nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex)
                }
                incompleteCard.allActionsSet()
            }
            "Fortune Teller" -> for (p in players) {
                if (p.userId != player!!.userId) {
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(p.userId)) {
                        game.addHistory(p.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                    } else if (!p.hasMoat() && !p.hasLighthouse()) {
                        val revealedCards = ArrayList<Card>()
                        val setAsideCards = ArrayList<Card>()
                        var foundVictoryOrCurse = false
                        var topDeckCard: Card? = null
                        while (!foundVictoryOrCurse) {
                            topDeckCard = p.removeTopDeckCard()
                            if (topDeckCard == null) {
                                break
                            }
                            revealedCards.add(topDeckCard)
                            if (topDeckCard.isVictory || topDeckCard.isCurseOnly) {
                                foundVictoryOrCurse = true
                                p.addCardToTopOfDeck(topDeckCard)
                            } else {
                                setAsideCards.add(topDeckCard)
                            }
                        }
                        if (!revealedCards.isEmpty()) {
                            game.addHistory(p.username, " revealed ", KingdomUtil.groupCards(revealedCards, true))
                            p.discard.addAll(setAsideCards)
                            for (c in setAsideCards) {
                                game.playerDiscardedCard(p, c)
                            }
                            game.refreshDiscard(p)
                            if (foundVictoryOrCurse) {
                                game.addHistory(KingdomUtil.getCardWithBackgroundColor(card), " added ", KingdomUtil.getArticleWithCardName(topDeckCard!!), " on top of ", p.username, "'s deck")
                            }
                        } else {
                            game.addHistory(p.username, " did not have any cards to draw")
                        }
                    } else {
                        if (p.hasLighthouse()) {
                            game.addHistory(p.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                        } else if (p.hasMoat()) {
                            game.addHistory(p.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                        }
                    }
                }
            }
            "Hamlet" -> when {
                player!!.hand.isEmpty() -> game.addHistory(player.username, " did not have any cards in ", player.pronoun, " hand")
                else -> {
                    val cardAction = CardAction(CardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Cornucopia
                    cardAction.cardName = card.name
                    cardAction.instructions = "Do you want to discard a card from your hand to gain +1 Action?"
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Harvest" -> {
                var hasMoreCards = true
                val revealedCards = ArrayList<Card>()
                val cardNames = HashSet<String>()
                while (hasMoreCards && revealedCards.size < 4) {
                    val c = player!!.removeTopDeckCard()
                    if (c == null) {
                        hasMoreCards = false
                    } else {
                        revealedCards.add(c)
                        cardNames.add(c.name)
                    }
                }
                if (!revealedCards.isEmpty()) {
                    game.addHistory(KingdomUtil.getCardWithBackgroundColor(card), " revealed ", KingdomUtil.groupCards(revealedCards, true))
                    player!!.discard.addAll(revealedCards)
                    for (c in revealedCards) {
                        game.playerDiscardedCard(player, c)
                    }
                    player.addCoins(cardNames.size)
                    game.refreshAllPlayersCardsBought()
                    game.addHistory(player.username, " gained +", KingdomUtil.getPlural(cardNames.size, "Coin"), " from ", KingdomUtil.getCardWithBackgroundColor(card))
                } else {
                    game.addHistory(player!!.username, " did not have any cards to reveal")
                }
            }
            "Horse Traders" -> when {
                player!!.hand.size > 0 -> if (player.hand.size == 1) {
                    game.addHistory(player.username, " discarded ", KingdomUtil.getArticleWithCardName(player.hand[0]))
                    player.discardCardFromHand(player.hand[0])
                } else {
                    val cardAction = CardAction(CardAction.TYPE_DISCARD_FROM_HAND)
                    cardAction.deck = Deck.Cornucopia
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 2
                    cardAction.instructions = "Select two cards to discard."
                    cardAction.cards = player.hand
                    game.setPlayerCardAction(player, cardAction)
                }
                else -> game.addHistory(player.username, " did not have any cards to discard")
            }
            "Hunting Party" -> {
                val cardNames = HashSet<String>()
                if (!player!!.hand.isEmpty()) {
                    game.addHistory(player.username, "'s current hand contains ", KingdomUtil.groupCards(player.hand, true))
                    for (c in player.hand) {
                        cardNames.add(c.name)
                    }
                } else {
                    game.addHistory(player.username, " did not have any cards in ", player.pronoun, " hand")
                }
                val revealedCards = ArrayList<Card>()
                val setAsideCards = ArrayList<Card>()
                var topDeckCard: Card? = null
                var foundCard = false
                while (!foundCard) {
                    topDeckCard = player.removeTopDeckCard()
                    if (topDeckCard == null) {
                        break
                    }
                    revealedCards.add(topDeckCard)
                    if (!cardNames.contains(topDeckCard.name)) {
                        foundCard = true
                        player.addCardToHand(topDeckCard)
                    } else {
                        setAsideCards.add(topDeckCard)
                    }
                }
                if (!revealedCards.isEmpty()) {
                    game.addHistory(player.username, " revealed ", KingdomUtil.groupCards(revealedCards, true))
                    player.discard.addAll(setAsideCards)
                    for (c in setAsideCards) {
                        game.playerDiscardedCard(player, c)
                    }
                    if (foundCard) {
                        game.addHistory(player.username, " added ", KingdomUtil.getArticleWithCardName(topDeckCard!!), " to ", player.pronoun, " hand")
                    } else {
                        game.addHistory(player.username, " did not have any cards that weren't duplicates of ones in ", player.pronoun, " hand")
                    }
                } else {
                    game.addHistory(player.username, " did not have any cards to draw")
                }
            }
            "Jester" -> {
                incompleteCard = SinglePlayerIncompleteCard(card.name, game)
                var nextPlayerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                while (nextPlayerIndex != game.currentPlayerIndex) {
                    val nextPlayer = players[nextPlayerIndex]
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                        game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        val topDeckCard = nextPlayer.removeTopDeckCard()
                        if (topDeckCard != null) {
                            nextPlayer.addCardToDiscard(topDeckCard)
                            game.playerDiscardedCard(nextPlayer, topDeckCard)
                            game.refreshDiscard(nextPlayer)
                            game.addHistory(KingdomUtil.getCardWithBackgroundColor(card), " discarded ", nextPlayer.username, "'s ", KingdomUtil.getCardWithBackgroundColor(topDeckCard))
                            if (topDeckCard.isVictory) {
                                if (game.supply[Card.CURSE_ID]!! > 0) {
                                    game.playerGainedCard(nextPlayer, game.curseCard)
                                }
                            } else {
                                if (game.supply[topDeckCard.cardId] == null || game.supply[topDeckCard.cardId] == 0) {
                                    game.addHistory("The supply did not have ", KingdomUtil.getArticleWithCardName(topDeckCard))
                                } else {
                                    val nextCardAction = CardAction(CardAction.TYPE_CHOICES)
                                    nextCardAction.deck = Deck.Cornucopia
                                    nextCardAction.cardName = card.name
                                    nextCardAction.instructions = "Your Jester discarded " + nextPlayer.username + "'s " + topDeckCard.name + ". Do you want to gain a copy of this card, or do you want " + nextPlayer.username + " to gain a copy of this card?"
                                    nextCardAction.cards.add(topDeckCard)
                                    nextCardAction.choices.add(CardActionChoice("I want it", "me"))
                                    nextCardAction.choices.add(CardActionChoice("Give one to " + nextPlayer.username, "them"))
                                    nextCardAction.playerId = nextPlayer.userId
                                    incompleteCard.extraCardActions.add(nextCardAction)
                                }
                            }
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
                    nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex)
                }
                game.refreshAllPlayersDiscard()
                if (!incompleteCard.extraCardActions.isEmpty()) {
                    val cardAction = incompleteCard.extraCardActions.remove()
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
            "Menagerie" -> {
                val cardNames = HashSet<String>()
                if (!player!!.hand.isEmpty()) {
                    game.addHistory(player.username, "'s current hand contains ", KingdomUtil.groupCards(player.hand, true))
                    for (c in player.hand) {
                        cardNames.add(c.name)
                    }
                    if (cardNames.size == player.hand.size) {
                        game.addHistory(player.username, "'s hand did not contain any duplicates")
                        game.addHistory(player.username, " gained +3 Cards")
                        player.drawCards(3)
                    } else {
                        game.addHistory(player.username, "'s hand contained duplicates")
                        game.addHistory(player.username, " gained +1 Card")
                        player.drawCards(1)
                    }
                } else {
                    game.addHistory(player.username, " did not have any cards in ", player.pronoun, " hand")
                }
            }
            "Princess" -> {
                game.princessCardPlayed()
                game.refreshAllPlayersSupply()
                game.refreshAllPlayersPlayingArea()
                game.refreshAllPlayersHandArea()
            }
            "Remake" -> when {
                player!!.hand.size > 0 -> {
                    val cardAction = CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Cornucopia
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    game.setPlayerCardAction(player, cardAction)
                    if (player.hand.size > 1) {
                        incompleteCard = SinglePlayerIncompleteCard(card.name, game)
                        val secondCardAction = CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND)
                        secondCardAction.deck = Deck.Cornucopia
                        secondCardAction.cardName = card.name
                        secondCardAction.buttonValue = "Done"
                        secondCardAction.numCards = 1
                        secondCardAction.instructions = "Select a card to trash."
                        secondCardAction.cards = player.hand
                        incompleteCard.extraCardActions.add(secondCardAction)
                    }
                }
                else -> game.addHistory(player.username, " did not have any cards in ", player.pronoun, " hand")
            }
            "Tournament" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, player!!.hasProvinceInHand())
                game.isGainTournamentBonus = true
                if (player.hasProvinceInHand()) {
                    val cardAction = CardAction(CardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Cornucopia
                    cardAction.cardName = card.name
                    cardAction.instructions = "Do you want to reveal and discard a Province to gain a Prize or a Duchy?"
                    game.setPlayerCardAction(player, cardAction)
                }
                for (p in players) {
                    if (p.userId != player.userId) {
                        if (p.hasProvinceInHand()) {
                            val cardAction = CardAction(CardAction.TYPE_YES_NO)
                            cardAction.deck = Deck.Cornucopia
                            cardAction.cardName = card.name
                            cardAction.instructions = "Do you want to reveal a Province to prevent " + player.username + " from gaining +1 Card, +1 Coin?"
                            game.setPlayerCardAction(p, cardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(p.userId)
                        }
                    }
                }
                game.addNextAction("gain bonuses")
                incompleteCard.allActionsSet()
            }
            "Trusty Steed" -> {
                val cardAction = CardAction(CardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Cornucopia
                cardAction.cardName = card.name
                cardAction.instructions = "Choose a combination."
                cardAction.choices.add(CardActionChoice("+2 Cards, +2 Actions", "cardsAndActions"))
                cardAction.choices.add(CardActionChoice("+2 Cards, +2 Coins", "cardsAndCoins"))
                cardAction.choices.add(CardActionChoice("+2 Cards, 4 Silvers", "cardsAndSilvers"))
                cardAction.choices.add(CardActionChoice("+2 Actions, +2 Coins", "actionsAndCoins"))
                cardAction.choices.add(CardActionChoice("+2 Actions, 4 Silvers", "actionsAndSilvers"))
                cardAction.choices.add(CardActionChoice("+2 Coins, 4 Silvers", "coinsAndSilvers"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Young Witch" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, player!!.hand.size >= 2)
                if (player.hand.size > 0) {
                    if (player.hand.size == 1) {
                        game.playerDiscardedCard(player, player.hand[0])
                        player.discardCardFromHand(player.hand[0])
                        game.addHistory(player.username, " discarded ", KingdomUtil.getArticleWithCardName(player.hand[0]))
                    } else {
                        val cardAction = CardAction(CardAction.TYPE_DISCARD_FROM_HAND)
                        cardAction.deck = Deck.Cornucopia
                        cardAction.cardName = card.name
                        cardAction.buttonValue = "Done"
                        cardAction.numCards = 2
                        cardAction.instructions = "Select two cards to discard."
                        cardAction.cards = player.hand
                        game.setPlayerCardAction(player, cardAction)
                    }
                } else {
                    game.addHistory(player.username, " did not have any cards to discard")
                }
                var nextPlayerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                while (nextPlayerIndex != game.currentPlayerIndex) {
                    val nextPlayer = players[nextPlayerIndex]
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                        incompleteCard.setPlayerActionCompleted(nextPlayer.userId)
                        game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        if (nextPlayer.hasBaneCardInHand()) {
                            val cardAction = CardAction(CardAction.TYPE_CHOICES)
                            cardAction.deck = Deck.Cornucopia
                            cardAction.cardName = card.name
                            cardAction.instructions = "Do you want to reveal your Bane card, or do you want to gain a Curse?"
                            cardAction.choices.add(CardActionChoice("Reveal Bane Card", "reveal"))
                            cardAction.choices.add(CardActionChoice("Gain a Curse", "curse"))
                            game.setPlayerCardAction(nextPlayer, cardAction)
                        } else {
                            if (game.supply[Card.CURSE_ID]!! > 0) {
                                game.playerGainedCard(nextPlayer, game.curseCard)
                                game.refreshDiscard(nextPlayer)
                            }
                            incompleteCard.setPlayerActionCompleted(nextPlayer.userId)
                        }
                    } else {
                        incompleteCard.setPlayerActionCompleted(nextPlayer.userId)
                        if (nextPlayer.hasLighthouse()) {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                        } else {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                        }
                    }
                    nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex)
                }
                incompleteCard.allActionsSet()
            }
        }

        return incompleteCard
    }
}