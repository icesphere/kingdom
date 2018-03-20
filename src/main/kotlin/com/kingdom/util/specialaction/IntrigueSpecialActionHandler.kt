package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardColor
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.Estate
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object IntrigueSpecialActionHandler {
    fun handleSpecialAction(game: OldGame, card: Card): IncompleteCard? {

        val cardMap = game.cardMap
        val supplyMap = game.supplyMap
        val players = game.players
        val currentPlayerId = game.currentPlayerId
        val currentPlayerIndex = game.currentPlayerIndex
        var incompleteCard: IncompleteCard? = null

        when (card.name) {
            "Baron" -> {
                val player = game.currentPlayer
                var hasEstate = false
                for (c in player!!.hand) {
                    if (c.name == Estate.NAME) {
                        hasEstate = true
                        break
                    }
                }
                if (hasEstate) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Intrigue
                    cardAction.cardName = card.name
                    cardAction.instructions = "Do you want to discard an Estate card from your hand? If you do then you get +4 coins, otherwise you gain an Estate card."
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    if (game.isCardInSupply(Estate.NAME)) {
                        game.playerGainedCard(player, game.estateCard)
                        game.refreshDiscard(player)
                    }
                }
            }
            "Bridge" -> {
                game.incrementCostDiscount()
                game.refreshAllPlayersSupply()
                game.refreshAllPlayersPlayingArea()
                game.refreshAllPlayersHandArea()
            }
            "Conspirator" -> if (game.numActionsCardsPlayed >= 3) {
                val player = game.currentPlayer
                player!!.drawCards(1)
                player.addActions(1)
                game.addHistory(player.username, " gained +1 Card, +1 Action from ", KingdomUtil.getWordWithBackgroundColor("Conspirator", CardColor.Action))
            }
            "Courtyard" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK)
                cardAction.deck = Deck.Intrigue
                cardAction.cardName = card.name
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.hand)
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select a card from your hand to put on top of your deck."
                if (!cardAction.cards.isEmpty()) {
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Ironworks" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
                cardAction.deck = Deck.Intrigue
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
            "Masquerade" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, true)
                game.addNextAction("trashCard")
                game.masqueradeCards.clear()
                for (player in players) {
                    if (player.hand.size > 0) {
                        val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                        cardAction.deck = Deck.Intrigue
                        cardAction.cardName = card.name
                        cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                        cardAction.numCards = 1
                        cardAction.instructions = "Choose a card to pass to the next player and then click Done."
                        cardAction.buttonValue = "Done"
                        game.setPlayerCardAction(player, cardAction)
                    } else {
                        incompleteCard.setPlayerActionCompleted(player.userId)
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Mining Village" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                cardAction.deck = Deck.Intrigue
                cardAction.cardName = card.name
                cardAction.cards.add(card)
                cardAction.instructions = "Do you want to trash this card to gain 2 coins?"
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Minion" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Intrigue
                cardAction.cardName = card.name
                cardAction.instructions = "Choose one: +2 coins OR Discard your hand and draw 4 cards, and each other player with at least 5 cards in hand discards their hand and draws 4 cards."
                cardAction.choices.add(CardActionChoice("+2 Coins", "coins"))
                cardAction.choices.add(CardActionChoice("Discard hand", "discard"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Nobles" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Intrigue
                cardAction.cardName = card.name
                cardAction.instructions = "Choose one: +3 Cards OR +2 Actions."
                cardAction.choices.add(CardActionChoice("+3 Cards", "cards"))
                cardAction.choices.add(CardActionChoice("+2 Actions", "actions"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Pawn" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Intrigue
                cardAction.cardName = card.name
                cardAction.instructions = "Choose a combination."
                cardAction.choices.add(CardActionChoice("+1 Card, +1 Action", "cardAndAction"))
                cardAction.choices.add(CardActionChoice("+1 Card, +1 Buy", "cardAndBuy"))
                cardAction.choices.add(CardActionChoice("+1 Card, +1 Coin", "cardAndCoin"))
                cardAction.choices.add(CardActionChoice("+1 Action, +1 Buy", "actionAndBuy"))
                cardAction.choices.add(CardActionChoice("+1 Action, +1 Coin", "actionAndCoin"))
                cardAction.choices.add(CardActionChoice("+1 Buy, +1 Coin", "buyAndCoin"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Saboteur" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (player in players) {
                    if (player.userId != currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                        } else if (!player.hasMoat() && !player.hasLighthouse()) {
                            val setAsideCards = ArrayList<Card>()
                            var c = player.removeTopDeckCard()
                            while (c != null && game.getCardCost(c) < 3) {
                                setAsideCards.add(c)
                                c = player.removeTopDeckCard()
                            }
                            player.discard.addAll(setAsideCards)
                            for (setAsideCard in setAsideCards) {
                                game.playerDiscardedCard(player, setAsideCard)
                            }
                            if (c != null) {
                                game.trashedCards.add(c)
                                game.playerLostCard(player, c)
                                val cardAction = OldCardAction(OldCardAction.TYPE_GAIN_UP_TO_FROM_SUPPLY)
                                cardAction.deck = Deck.Intrigue
                                cardAction.cardName = card.name
                                cardAction.buttonValue = "Done"
                                cardAction.numCards = 1
                                cardAction.instructions = "The Saboteur trashed your " + c.name + ". Select one of the following cards to gain and then click Done. If you don't want to gain a card just click Done."
                                game.addHistory("The ", KingdomUtil.getWordWithBackgroundColor("Saboteur", CardColor.Action), " trashed ", player.username, "'s ", c.name)
                                val highestCost = game.getCardCost(c) - 2
                                for (cardToGain in supplyMap.values) {
                                    if (game.getCardCost(cardToGain) <= highestCost && (c.costIncludesPotion || !cardToGain.costIncludesPotion) && game.isCardInSupply(cardToGain)) {
                                        cardAction.cards.add(cardToGain)
                                    }
                                }
                                if (cardAction.cards.size > 0) {
                                    game.setPlayerCardAction(player, cardAction)
                                } else {
                                    incompleteCard.setPlayerActionCompleted(player.userId)
                                }
                            } else {
                                game.addHistory(player.username, " did not have any cards costing 3 coins or more to trash")
                                incompleteCard.setPlayerActionCompleted(player.userId)
                            }
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            if (player.hasLighthouse()) {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                            } else {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Scout" -> {
                val player = game.currentPlayer
                val cards = ArrayList<Card>()
                var hasMoreCards = true
                val revealedCards = ArrayList<Card>()
                while (hasMoreCards && revealedCards.size < 4) {
                    val c = player!!.removeTopDeckCard()
                    if (c == null) {
                        hasMoreCards = false
                    } else {
                        revealedCards.add(c)
                        if (c.isVictory) {
                            player.addCardToHand(c)
                        } else {
                            cards.add(c)
                        }
                    }
                }
                if (!cards.isEmpty()) {
                    game.addHistory(KingdomUtil.getCardWithBackgroundColor(card), " revealed ", KingdomUtil.groupCards(revealedCards, true))
                } else {
                    game.addHistory(player!!.username, " did not have any cards to reveal")
                }
                if (cards.size == 1) {
                    player!!.addCardToTopOfDeck(cards[0])
                } else if (cards.size > 0) {
                    game.refreshHand(player!!)
                    val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER)
                    cardAction.deck = Deck.Intrigue
                    cardAction.isHideOnSelect = true
                    cardAction.numCards = cards.size
                    cardAction.cardName = card.name
                    cardAction.cards = cards
                    cardAction.buttonValue = "Done"
                    cardAction.instructions = "Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)"
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Secret Chamber" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_DISCARD_UP_TO_FROM_HAND)
                cardAction.deck = Deck.Intrigue
                cardAction.cardName = card.name
                cardAction.cards.addAll(player!!.hand)
                cardAction.numCards = player.hand.size
                cardAction.instructions = "Discard any number of cards. +1 Coin per card discarded. Select the Cards you want to discard and then click Done."
                cardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, cardAction)
            }
            "Shanty Town" -> {
                val player = game.currentPlayer
                var hasAction = false
                for (c in player!!.hand) {
                    if (c.isAction) {
                        hasAction = true
                    }
                }
                if (!hasAction) {
                    player.drawCards(2)
                    game.addHistory(player.username, " did not have any action cards and got +2 cards.")
                }
            }
            "Steward" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Intrigue
                cardAction.cardName = card.name
                cardAction.instructions = "Choose one:"
                cardAction.choices.add(CardActionChoice("+2 Cards", "cards"))
                cardAction.choices.add(CardActionChoice("+2 Coins", "coins"))
                cardAction.choices.add(CardActionChoice("Trash 2 Cards", "trash"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Swindler" -> {
                incompleteCard = SinglePlayerIncompleteCard(card.name, game)
                val currentPlayer = game.currentPlayer
                var nextPlayerIndex = game.nextPlayerIndex
                while (nextPlayerIndex != currentPlayerIndex) {
                    val nextPlayer = players[nextPlayerIndex]
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                        game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        val topCard = nextPlayer.removeTopDeckCard()
                        if (topCard != null) {
                            game.trashedCards.add(topCard)
                            game.playerLostCard(nextPlayer, topCard)
                            game.addHistory("The ", KingdomUtil.getWordWithBackgroundColor("Swindler", CardColor.Action), " trashed ", nextPlayer.username, "'s ", KingdomUtil.getCardWithBackgroundColor(topCard))
                            val nextCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                            nextCardAction.deck = Deck.Intrigue
                            nextCardAction.numCards = 1
                            nextCardAction.buttonValue = "Done"
                            nextCardAction.cardName = card.name
                            nextCardAction.instructions = "You trashed " + nextPlayer.username + "'s " + topCard.name + ". Select the card you want to give to " + nextPlayer.username + " and then click Done."
                            nextCardAction.playerId = nextPlayer.userId
                            val cost = game.getCardCost(topCard)
                            for (c in supplyMap.values) {
                                if (game.getCardCost(c) == cost && c.costIncludesPotion == topCard.costIncludesPotion && game.isCardInSupply(c)) {
                                    nextCardAction.cards.add(c)
                                }
                            }
                            if (nextCardAction.cards.size > 0) {
                                incompleteCard.extraOldCardActions.add(nextCardAction)
                            } else {
                                game.addHistory(nextPlayer.username, " did not have any cards in the supply to gain")
                            }
                        } else {
                            game.addHistory(nextPlayer.username, " did not have a card to draw")
                        }
                    } else {
                        if (nextPlayer.hasLighthouse()) {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                        } else {
                            game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
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
                    cardAction.deck = Deck.Intrigue
                    game.setPlayerCardAction(currentPlayer!!, cardAction)
                }
            }
            "Torturer" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (player in players) {
                    if (player.userId != currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                        } else if (!player.hasMoat() && !player.hasLighthouse()) {
                            val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                            cardAction.deck = Deck.Intrigue
                            cardAction.cardName = card.name
                            cardAction.instructions = "Choose one: Discard 2 cards OR Gain a Curse card into your hand."
                            cardAction.choices.add(CardActionChoice("Discard", "discard"))
                            cardAction.choices.add(CardActionChoice("Curse", "curse"))
                            game.setPlayerCardAction(player, cardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            if (player.hasLighthouse()) {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                            } else {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Trading Post" -> {
                val player = game.currentPlayer
                if (player!!.hand.size == 1) {
                    game.trashedCards.add(player.hand[0])
                    game.playerLostCard(player, player.hand[0])
                    player.removeCardFromHand(player.hand[0])
                    game.addHistory("Trading Post trashed the last card in ", player.username, "'s hand")
                } else if (player.hand.size > 1) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Intrigue
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 2
                    cardAction.instructions = "Select two cards to trash."
                    cardAction.cards = player.hand
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Tribute" -> {
                val player = game.currentPlayer
                val nextPlayer = players[game.nextPlayerIndex]
                val firstCard = nextPlayer.removeTopDeckCard()
                val secondCard = nextPlayer.removeTopDeckCard()
                firstCard?.let {
                    nextPlayer.addCardToDiscard(firstCard)
                    game.playerDiscardedCard(nextPlayer, firstCard)
                }
                secondCard?.let {
                    nextPlayer.addCardToDiscard(secondCard)
                    game.playerDiscardedCard(nextPlayer, secondCard)
                }

                val cards = ArrayList<Card>(2)
                if (firstCard != null) {
                    cards.add(firstCard)
                }
                if (firstCard != null && secondCard != null && firstCard.name != secondCard.name) {
                    cards.add(secondCard)
                }
                for (c in cards) {
                    if (c.isAction) {
                        player!!.addActions(2)
                    }
                    if (c.isTreasure) {
                        player!!.addCoins(2)
                    }
                    if (c.isVictory) {
                        player!!.drawCards(2)
                    }
                }

                if (firstCard != null && secondCard != null) {
                    game.addHistory("The top two cards of ", nextPlayer.username, "'s deck for tribute were ", KingdomUtil.getArticleWithCardName(firstCard), " and ", KingdomUtil.getArticleWithCardName(secondCard))
                    game.refreshDiscard(nextPlayer)
                }
            }
            "Upgrade" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Intrigue
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.addHistory(player.username, " did not have any cards in ", player.pronoun, " hand")
                }
            }
            "Wishing Well" -> {
                val player = game.currentPlayer
                if (player!!.deck.size + player.discard.size == 0) {
                    game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("Your deck and discard piles are empty."))
                } else {
                    val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                    cardAction.deck = Deck.Intrigue
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select the card that you think will be on top of your deck."
                    cardAction.cards.addAll(cardMap.values)
                    if (cardAction.cards.size > 0) {
                        game.setPlayerCardAction(player, cardAction)
                    }
                }
            }
        }

        return incompleteCard
    }
}