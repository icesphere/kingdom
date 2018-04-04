package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardColor
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Curse
import com.kingdom.util.KingdomUtil

import java.util.*

object ProsperitySpecialActionHandler {
    fun handleSpecialAction(game: OldGame, card: Card): IncompleteCard? {

        val players = game.players
        val currentPlayerId = game.currentPlayerId
        var incompleteCard: IncompleteCard? = null

        when (card.name) {
            "Bishop" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Prosperity
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "City" -> {
                val player = game.currentPlayer
                if (game.numEmptyPiles >= 1) {
                    player!!.drawCards(1)
                }
                if (game.numEmptyPiles >= 2) {
                    player!!.addCoins(1)
                    player.addBuys(1)
                }
            }
            "Counting House" -> {
                val player = game.currentPlayer
                var numCoppersInDiscard = 0
                for (c in player!!.discard) {
                    if (c.isCopper) {
                        numCoppersInDiscard++
                    }
                }
                if (numCoppersInDiscard > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_NUMBER_BETWEEN)
                    cardAction.deck = Deck.Prosperity
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.startNumber = 0
                    cardAction.endNumber = numCoppersInDiscard
                    cardAction.instructions = "Click the number of Coppers you want to add to your hand from your discard pile."
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("There were no Coppers in your discard pile."))
                }
            }
            "Expand" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Prosperity
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Forge" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND)
                    cardAction.deck = Deck.Prosperity
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = player.hand.size
                    cardAction.instructions = "Select the cards you want to trash. You will then gain a card in cost exactly equal to the total cost in coins of the trashed cards."
                    cardAction.cards = player.hand
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Goons" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (player in players) {
                    if (player.userId != currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                        } else if (!player.hasMoat() && player.hand.size > 3 && !player.hasLighthouse()) {
                            val cardAction = OldCardAction(OldCardAction.TYPE_DISCARD_DOWN_TO_FROM_HAND)
                            cardAction.deck = Deck.Prosperity
                            cardAction.cardName = card.name
                            cardAction.cards.addAll(player.hand)
                            cardAction.numCards = 3
                            cardAction.instructions = "Discard down to 3 cards. Select the Cards you want to discard and then click Done."
                            cardAction.buttonValue = "Done"
                            game.setPlayerCardAction(player, cardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            when {
                                player.hasLighthouse() -> game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                                player.hasMoat() -> game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                                else -> game.addHistory(player.username, " had 3 or less cards")
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "King's Court" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_UP_TO)
                cardAction.deck = Deck.Prosperity
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select one of the following actions to play three times and then click Done, or just click Done if you don't want to select an action."
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.actionCards)
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Mint" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Prosperity
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select the treasure card you want to gain a copy of."
                val treasureCards = HashSet<Card>()
                for (c in player!!.hand) {
                    if (c.isTreasure && game.isCardInSupply(c)) {
                        treasureCards.add(c)
                    }
                }
                cardAction.cards.addAll(treasureCards)
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Mountebank" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (player in players) {
                    if (player.userId != currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                        } else if (!player.hasMoat() && !player.hasLighthouse()) {
                            if (player.curseCardsInHand > 0) {
                                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                                cardAction.deck = Deck.Prosperity
                                cardAction.cardName = card.name
                                cardAction.instructions = "Choose one: Discard a curse OR gain a Curse and a Copper."
                                cardAction.choices.add(CardActionChoice("Discard Curse", "discard"))
                                cardAction.choices.add(CardActionChoice("Gain Cards", "gain"))
                                game.setPlayerCardAction(player, cardAction)
                            } else {
                                incompleteCard.setPlayerActionCompleted(player.userId)
                                if (game.isCardInSupply(Curse.NAME)) {
                                    game.playerGainedCard(player, game.curseCard)
                                }
                                if (game.isCardInSupply(Copper.NAME)) {
                                    game.playerGainedCard(player, game.copperCard)
                                }
                                game.refreshDiscard(player)
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
            "Rabble" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (player in players) {
                    if (player.userId != currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                        } else if (!player.hasMoat() && !player.hasLighthouse()) {
                            val cardsRevealed = ArrayList<Card>()
                            while (cardsRevealed.size < 3) {
                                val topDeckCard = player.removeTopDeckCard() ?: break
                                cardsRevealed.add(topDeckCard)
                            }
                            if (cardsRevealed.size > 0) {
//                                game.addHistory("The top cards from ", player.username, "'s deck were ", KingdomUtil.getCardNames(cardsRevealed))
                                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER)
                                cardAction.deck = Deck.Prosperity
                                for (c in cardsRevealed) {
                                    if (c.isTreasure || c.isAction) {
                                        player.addCardToDiscard(c)
                                        game.playerDiscardedCard(player, c)
                                    } else {
                                        cardAction.cards.add(c)
                                    }
                                }
                                if (cardAction.cards.size == 1) {
                                    incompleteCard.setPlayerActionCompleted(player.userId)
                                    player.addCardToTopOfDeck(cardAction.cards[0])
                                    game.addHistory(player.username, " put one card back on top of " + player.pronoun, " deck")
                                } else if (cardAction.cards.size > 0) {
                                    game.addHistory(player.username, " put " + cardAction.cards.size + " cards back on top of " + player.pronoun, " deck")
                                    cardAction.isHideOnSelect = true
                                    cardAction.numCards = cardAction.cards.size
                                    cardAction.cardName = card.name
                                    cardAction.buttonValue = "Done"
                                    cardAction.instructions = "Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)"
                                    game.setPlayerCardAction(player, cardAction)
                                } else {
                                    incompleteCard.setPlayerActionCompleted(player.userId)
                                }
                            } else {
                                incompleteCard.setPlayerActionCompleted(player.userId)
                            }
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            if (player.hasLighthouse()) {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                            } else if (player.hasMoat()) {
                                game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Trade Route" -> {
                val player = game.currentPlayer
                if (game.tradeRouteTokensOnMat > 0) {
                    player!!.addCoins(game.tradeRouteTokensOnMat)
                    game.addHistory(player.username, " gained +", KingdomUtil.getPlural(game.tradeRouteTokensOnMat, "Coin"), " from playing Trade Route")
                }
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Prosperity
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Vault" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_DISCARD_UP_TO_FROM_HAND)
                cardAction.deck = Deck.Prosperity
                cardAction.cardName = card.name
                cardAction.cards.addAll(player!!.hand)
                cardAction.numCards = player.hand.size
                cardAction.instructions = "Discard any number of cards. +1 Coin per card discarded. Select the Cards you want to discard and then click Done."
                cardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, cardAction)
            }
            "Watchtower" -> {
                val player = game.currentPlayer
                if (player!!.hand.size < 6) {
                    player.drawCards(6 - player.hand.size)
                    game.refreshHand(player)
                }
            }
        }

        return incompleteCard
    }
}
