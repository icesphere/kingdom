package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object HinterlandsSpecialActionHandler {

    fun handleSpecialAction(game: Game, card: Card): IncompleteCard? {
        val player = game.currentPlayer
        var incompleteCard: IncompleteCard? = null

        when (card.name) {
            "Cartographer" -> {
                val cards = ArrayList<Card>()
                while (cards.size < 4) {
                    val c = player!!.removeTopDeckCard() ?: break
                    cards.add(c)
                }
                if (!cards.isEmpty()) {
                    val cardAction = CardAction(CardAction.TYPE_DISCARD_UP_TO)
                    cardAction.deck = Deck.Hinterlands
                    cardAction.cardName = card.name
                    cardAction.cards = cards
                    cardAction.numCards = cards.size
                    cardAction.instructions = "Select the cards you want to discard and then click Done."
                    cardAction.buttonValue = "Done"
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
            "Crossroads" -> if (!player!!.hand.isEmpty()) {
                game.addHistory(player.username, "'s hand contains ", KingdomUtil.groupCards(player.hand, true))
                if (!player.getVictoryCards().isEmpty()) {
                    game.addHistory(player.username, " gained +", KingdomUtil.getPlural(player.getVictoryCards().size, "card"))
                    player.drawCards(player.getVictoryCards().size)
                    game.refreshAllPlayersCardsBought()
                }
                if (game.crossroadsPlayed == 1) {
                    game.addHistory(player.username, " gained +3 Actions")
                    player.addActions(3)
                    game.refreshAllPlayersCardsPlayed()
                }
            }
            "Develop" -> if (player!!.hand.size > 0) {
                val cardAction = CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND)
                cardAction.deck = Deck.Hinterlands
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select a card to trash."
                cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                game.setPlayerCardAction(player, cardAction)
            } else {
                game.addHistory(player.username, " did not have any cards in ", player.pronoun, " hand")
            }
            "Duchess" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, true)
                for (p in game.players) {
                    val topDeckCard = p.removeTopDeckCard()
                    if (topDeckCard != null) {
                        val cardAction = CardAction(CardAction.TYPE_CHOICES)
                        cardAction.deck = Deck.Hinterlands
                        cardAction.cardName = card.name
                        cardAction.associatedCard = topDeckCard
                        cardAction.cards.add(topDeckCard)
                        cardAction.instructions = "Do you want to discard the top card of your deck, or put it back?"
                        cardAction.choices.add(CardActionChoice("Discard", "discard"))
                        cardAction.choices.add(CardActionChoice("Put it back", "back"))
                        game.setPlayerCardAction(p, cardAction)
                    } else {
                        incompleteCard.setPlayerActionCompleted(p.userId)
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Embassy" -> if (player!!.hand.size > 0) {
                if (player.hand.size == 1) {
                    game.addHistory(player.username, " discarded 1 card")
                    game.playerDiscardedCard(player, player.hand[0])
                    player.discardCardFromHand(player.hand[0])
                } else {
                    var cardsToDiscard = 3
                    if (player.hand.size < 3) {
                        cardsToDiscard = player.hand.size
                    }
                    val cardAction = CardAction(CardAction.TYPE_DISCARD_FROM_HAND)
                    cardAction.deck = Deck.Hinterlands
                    cardAction.cardName = card.name
                    cardAction.cards.addAll(player.hand)
                    cardAction.numCards = cardsToDiscard
                    cardAction.instructions = "Select " + KingdomUtil.getPlural(cardsToDiscard, "card") + " to discard and then click Done."
                    cardAction.buttonValue = "Done"
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Inn" -> if (player!!.hand.size > 0) {
                if (player.hand.size == 1) {
                    game.addHistory(player.username, " discarded 1 card")
                    game.playerDiscardedCard(player, player.hand[0])
                    player.discardCardFromHand(player.hand[0])
                } else {
                    val cardAction = CardAction(CardAction.TYPE_DISCARD_FROM_HAND)
                    cardAction.deck = Deck.Hinterlands
                    cardAction.cardName = card.name
                    cardAction.cards.addAll(player.hand)
                    cardAction.numCards = 2
                    cardAction.instructions = "Select 2 cards to discard and then click Done."
                    cardAction.buttonValue = "Done"
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Jack of all Trades" -> {
                if (game.isCardInSupply(Card.SILVER_ID)) {
                    game.playerGainedCard(player!!, game.silverCard)
                }
                val topDeckCard = player!!.removeTopDeckCard()
                if (topDeckCard != null) {
                    val cardAction = CardAction(CardAction.TYPE_CHOICES)
                    cardAction.deck = Deck.Hinterlands
                    cardAction.cardName = card.name
                    cardAction.associatedCard = topDeckCard
                    cardAction.cards.add(topDeckCard)
                    cardAction.instructions = "Do you want to discard the top card of your deck, or put it back?"
                    cardAction.choices.add(CardActionChoice("Discard", "discard"))
                    cardAction.choices.add(CardActionChoice("Put it back", "back"))
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    while (player.hand.size < 5) {
                        val topCard = player.removeTopDeckCard() ?: break
                        player.addCardToHand(topCard)
                    }
                    game.refreshHand(player)
                    val cards = player.hand.filterNot { it.isTreasure }
                    if (!cards.isEmpty()) {
                        val trashCardAction = CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND)
                        trashCardAction.deck = Deck.Hinterlands
                        trashCardAction.cardName = card.name
                        trashCardAction.numCards = 1
                        trashCardAction.cards.addAll(KingdomUtil.uniqueCardList(cards))
                        trashCardAction.instructions = "Select a card to trash from your hand and then click Done, or just click Done if you don't want to trash a card."
                        trashCardAction.buttonValue = "Done"
                        game.setPlayerCardAction(player, trashCardAction)
                    }
                }
            }
            "Mandarin" -> {
                val cardAction = CardAction(CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK)
                cardAction.deck = Deck.Hinterlands
                cardAction.cardName = card.name
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.hand)
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select a card from your hand to put on top of your deck."
                if (!cardAction.cards.isEmpty()) {
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Margrave" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (p in game.players) {
                    if (!game.isCurrentPlayer(p)) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(p.userId)) {
                            incompleteCard.setPlayerActionCompleted(p.userId)
                            game.addHistory(p.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                        } else if (!p.hasMoat() && !p.hasLighthouse()) {
                            p.drawCards(1)
                            game.addHistory(p.username, " drew 1 card")
                            game.refreshHand(p)
                            if (p.hand.size > 3) {
                                val cardAction = CardAction(CardAction.TYPE_DISCARD_DOWN_TO_FROM_HAND)
                                cardAction.deck = Deck.Hinterlands
                                cardAction.cardName = card.name
                                cardAction.cards.addAll(p.hand)
                                cardAction.numCards = 3
                                cardAction.instructions = "Discard down to 3 cards. Select the Cards you want to discard and then click Done."
                                cardAction.buttonValue = "Done"
                                game.setPlayerCardAction(p, cardAction)
                            } else {
                                incompleteCard.setPlayerActionCompleted(p.userId)
                                game.addHistory(p.username, " had 3 or less cards")
                            }
                        } else {
                            incompleteCard.setPlayerActionCompleted(p.userId)
                            if (p.hasLighthouse()) {
                                game.addHistory(p.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                            } else if (p.hasMoat()) {
                                game.addHistory(p.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Noble Brigand" -> BuySpecialActionHandler.setNobleBrigandCardAction(game, player!!)
            "Oasis" -> if (player!!.hand.size > 0) {
                if (player.hand.size == 1) {
                    game.addHistory(player.username, " discarded 1 card")
                    game.playerDiscardedCard(player, player.hand[0])
                    player.discardCardFromHand(player.hand[0])
                } else {
                    val cardAction = CardAction(CardAction.TYPE_DISCARD_FROM_HAND)
                    cardAction.deck = Deck.Hinterlands
                    cardAction.cardName = card.name
                    cardAction.cards.addAll(player.hand)
                    cardAction.numCards = 1
                    cardAction.instructions = "Select 1 card to discard and then click Done."
                    cardAction.buttonValue = "Done"
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Oracle" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, true)
                var playersProcessed = 0
                var playerIndex = game.currentPlayerIndex
                while (playersProcessed < game.numPlayers) {
                    val p = game.players[playerIndex]
                    val topCards = ArrayList<Card>()
                    val firstTopCard = p.removeTopDeckCard()
                    if (firstTopCard != null) {
                        val cardAction = CardAction(CardAction.TYPE_CHOICES)
                        cardAction.deck = Deck.Hinterlands
                        cardAction.cardName = card.name
                        cardAction.choices.add(CardActionChoice("Discard", "discard"))
                        cardAction.playerId = p.userId

                        topCards.add(firstTopCard)
                        val secondTopCard = p.removeTopDeckCard()
                        if (secondTopCard != null) {
                            cardAction.choices.add(CardActionChoice("Put them back", "back"))
                            topCards.add(secondTopCard)
                            game.addHistory(p.username, " revealed ", KingdomUtil.groupCards(topCards, true))
                            if (game.isCurrentPlayer(p)) {
                                cardAction.instructions = "Do you want to discard the top two cards of your deck, or put them back?"
                            } else {
                                cardAction.instructions = "Do you want to discard the top two cards of " + p.username + "'s deck, or put them back?"
                            }
                        } else {
                            cardAction.choices.add(CardActionChoice("Put it back", "back"))
                            game.addHistory(p.username, " revealed ", KingdomUtil.getCardWithBackgroundColor(firstTopCard, true))
                            if (game.isCurrentPlayer(p)) {
                                cardAction.instructions = "Do you want to discard the top card of your deck, or put it back?"
                            } else {
                                cardAction.instructions = "Do you want to discard the top card of " + p.username + "'s deck, or put it back?"
                            }
                        }
                        cardAction.cards.addAll(topCards)
                        incompleteCard.extraCardActions.add(cardAction)
                    } else {
                        game.addHistory(p.username, " did not have any cards to reveal")
                        incompleteCard.setPlayerActionCompleted(p.userId)
                    }

                    playerIndex = game.calculateNextPlayerIndex(playerIndex)
                    playersProcessed++
                }

                if (!incompleteCard.extraCardActions.isEmpty()) {
                    val cardAction = incompleteCard.extraCardActions.remove()
                    game.setPlayerCardAction(player!!, cardAction)
                } else {
                    player!!.drawCards(2)
                }

                incompleteCard.allActionsSet()
            }
            "Spice Merchant" -> {
                val cardAction = CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND)
                cardAction.deck = Deck.Hinterlands
                cardAction.cardName = card.name
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.treasureCards)
                cardAction.numCards = 1
                cardAction.instructions = "Select a treasure card to trash and then click Done, or just click Done if you don't want to trash a card."
                cardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, cardAction)
            }
            "Stables" -> {
                val cardAction = CardAction(CardAction.TYPE_DISCARD_UP_TO_FROM_HAND)
                cardAction.deck = Deck.Hinterlands
                cardAction.cardName = card.name
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.treasureCards)
                cardAction.numCards = 1
                cardAction.instructions = "Select a treasure card to discard and then click Done, or just click Done if you don't want to discard a card."
                cardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, cardAction)
            }
            "Trader" -> if (player!!.hand.size > 0) {
                val cardAction = CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND)
                cardAction.deck = Deck.Hinterlands
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

        return incompleteCard
    }
}
