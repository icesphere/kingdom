package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardColor
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil
import com.kingdom.util.specialaction.SpecialActionHandler

object NextActionHandler {
    fun handleAction(game: OldGame, cardName: String) {

        var nextAction = game.nextAction
        if (nextAction == null) {
            nextAction = ""
        }

        when (nextAction) {
            "check horse traders" -> {
                game.removeNextAction()
                val incompleteCard = MultiPlayerIncompleteCard("Horse Traders", game, false)
                val currentPlayer = game.currentPlayer
                var hasReaction = false
                game.players
                        .filter { it.userId != game.currentPlayerId }
                        .forEach {
                            if (it.hasHorseTradersInHand()) {
                                hasReaction = true
                                val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                                cardAction.deck = Deck.Cornucopia
                                cardAction.cardName = "Horse Traders"
                                cardAction.instructions = currentPlayer!!.username + " played " + KingdomUtil.getArticleWithCardName(game.attackCard!!) + ". Do you want to set aside your Horse Traders?"
                                game.setPlayerCardAction(it, cardAction)
                            } else {
                                incompleteCard.setPlayerActionCompleted(it.userId)
                            }
                        }
                incompleteCard.allActionsSet()
                if (!hasReaction) {
                    handleAction(game, "reaction")
                }
            }
            "check secret chamber" -> {
                game.removeNextAction()
                val incompleteCard = MultiPlayerIncompleteCard("Secret Chamber", game, false)
                val currentPlayer = game.currentPlayer
                var hasReaction = false
                game.players
                        .filter { it.userId != game.currentPlayerId }
                        .forEach {
                            if (it.hasSecretChamber()) {
                                hasReaction = true
                                val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                                cardAction.deck = Deck.Intrigue
                                cardAction.cardName = "Secret Chamber"
                                cardAction.instructions = currentPlayer!!.username + " played " + KingdomUtil.getArticleWithCardName(game.attackCard!!) + ", do you want to use your Secret Chamber?"
                                game.setPlayerCardAction(it, cardAction)
                            } else {
                                incompleteCard.setPlayerActionCompleted(it.userId)
                            }
                        }
                incompleteCard.allActionsSet()
                if (!hasReaction) {
                    handleAction(game, "reaction")
                }
            }
            "finish attack" -> {
                game.removeNextAction()
                game.removeIncompleteCard()
                SpecialActionHandler.handleSpecialAction(game, game.attackCard!!)
            }
        }
        when (cardName) {
            "Black Market" -> {
                val player = game.currentPlayer!!
                if (game.blackMarketTreasureQueue.isEmpty()) {
                    game.removeNextAction()
                    val buyCardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS)
                    buyCardAction.deck = Deck.Promo
                    for (card in game.blackMarketCardsToBuy) {
                        if (card.name == "Grand Market") {
                            val addCard = game.blackMarketTreasureCardsPlayed.none { it.isCopper }
                            if (addCard && game.canBuyCard(player, card)) {
                                buyCardAction.cards.add(card)
                            }
                        } else if (game.canBuyCardNotInSupply(player, card)) {
                            buyCardAction.cards.add(card)
                        }
                    }
                    if (buyCardAction.cards.size > 0) {
                        buyCardAction.cardName = "Black Market"
                        buyCardAction.numCards = 1
                        buyCardAction.buttonValue = "Done"
                        buyCardAction.instructions = "Click on the card you want to buy and then click Done."
                        game.setPlayerCardAction(player, buyCardAction)
                    } else {
                        val sortCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER)
                        sortCardAction.deck = Deck.Promo
                        sortCardAction.isHideOnSelect = true
                        sortCardAction.numCards = game.blackMarketCardsToBuy.size
                        sortCardAction.cards = game.blackMarketCardsToBuy
                        sortCardAction.buttonValue = "Done"
                        sortCardAction.instructions = "You don't have enough coins to buy any of these black market cards. Click the cards in the order you want them to be on the bottom of the black market deck, starting with the top card and then click Done. (The last card you click will be the bottom card of the black market deck)"
                        game.setPlayerCardAction(player, sortCardAction)
                    }
                } else {
                    var treasureCard: Card? = game.blackMarketTreasureQueue.remove()
                    while (treasureCard!!.isAutoPlayTreasure) {
                        game.playTreasureCard(player, treasureCard, true, true, false, true, true)
                        if (game.blackMarketTreasureQueue.isEmpty()) {
                            treasureCard = null
                            break
                        }
                        treasureCard = game.blackMarketTreasureQueue.remove()
                    }
                    game.playTreasureCard(player, treasureCard, true, true, false, true, true)
                }
            }
            "Hamlet" -> {
                game.removeNextAction()
                val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                cardAction.deck = Deck.Cornucopia
                cardAction.cardName = "Hamlet2"
                cardAction.instructions = "Do you want to discard a card from your hand to gain +1 Buy?"
                game.setPlayerCardAction(game.currentPlayer!!, cardAction)
            }
            "Masquerade" -> {
                game.removeNextAction()
                val players = game.players
                for (i in players.indices) {
                    val p = players[i]
                    val card = game.masqueradeCards[p.userId]
                    if (card != null) {
                        val nextPlayerIndex = game.calculateNextPlayerIndex(i)
                        val nextPlayer = players[nextPlayerIndex]
                        if (nextPlayer.userId == game.currentPlayerId && card.addCoins != 0) {
                            game.refreshAllPlayersCardsBought()
                        }
                        nextPlayer.addCardToHand(card)
                    }
                }
                val currentPlayer = game.currentPlayer!!
                val trashCardAction = OldCardAction(OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND).apply {
                    deck = Deck.Intrigue
                    cards.addAll(currentPlayer.hand)
                    numCards = 1
                    this.cardName = "Masquerade"
                    instructions = "Select a card to trash and then click Done, or just click Done if you don't want to trash a card."
                    buttonValue = "Done"
                }
                currentPlayer.isShowCardAction = false
                game.setPlayerCardAction(currentPlayer, trashCardAction)
                game.refreshAllPlayersHand()
            }
            "Tournament" -> {
                game.removeNextAction()
                if (game.isGainTournamentBonus) {
                    game.isGainTournamentBonus = false
                    game.currentPlayer!!.drawCards(1)
                    game.currentPlayer!!.addCoins(1)
                    game.refreshAllPlayersCardsBought()
                    game.addHistory(game.currentPlayer!!.username, " gained +1 Card, +1 Coin from the ", KingdomUtil.getWordWithBackgroundColor("Tournament", CardColor.Action))
                }
            }
            else -> {
                val error = GameError(GameError.GAME_ERROR, "unknown next action - card: $cardName next action: $nextAction")
                game.logError(error, false)
                game.removeNextAction()
            }
        }
    }
}
