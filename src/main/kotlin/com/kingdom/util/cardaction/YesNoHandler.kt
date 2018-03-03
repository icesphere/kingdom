package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object YesNoHandler {
    fun handleCardAction(game: Game, player: Player, cardAction: CardAction, yesNoAnswer: String): IncompleteCard? {

        val playerMap = game.playerMap
        var incompleteCard: IncompleteCard? = null

        when (cardAction.cardName) {
            "Baron" -> when (yesNoAnswer) {
                "yes" -> {
                    player.discardCardFromHand(Card.ESTATE_ID)
                    player.addCoins(4)
                    game.addHistory(player.username, " discarded an ", KingdomUtil.getWordWithBackgroundColor("Estate", Card.VICTORY_COLOR), " and got +4 coins")
                }
                else -> if (game.isCardInSupply(Card.ESTATE_ID)) {
                    game.playerGainedCard(player, game.estateCard)
                    game.refreshDiscard(player)
                }
            }
            "Black Market" -> when (yesNoAnswer) {
                "yes" -> when {
                    player.treasureCards.isNotEmpty() -> {
                        val playTreasureCardsAction = CardAction(CardAction.TYPE_CHOOSE_IN_ORDER)
                        playTreasureCardsAction.deck = Deck.Promo
                        playTreasureCardsAction.isHideOnSelect = true
                        playTreasureCardsAction.numCards = -1
                        playTreasureCardsAction.cardName = "Black Market Treasure"
                        playTreasureCardsAction.cards = player.treasureCards
                        playTreasureCardsAction.buttonValue = "Done"
                        playTreasureCardsAction.instructions = "Click the treasure cards you want to play in the order you want to play them, and then click Done."
                        game.setPlayerCardAction(player, playTreasureCardsAction)
                    }
                    else -> {
                        incompleteCard = SinglePlayerIncompleteCard("Black Market", game)
                        game.addNextAction("Buy Card")
                        incompleteCard.setPlayerActionCompleted(player.userId)
                    }
                }
                else -> {
                    game.addHistory(player.username, " chose to not buy a card from the black market deck")
                    val chooseOrderCardAction = CardAction(CardAction.TYPE_CHOOSE_IN_ORDER)
                    chooseOrderCardAction.deck = Deck.Promo
                    chooseOrderCardAction.isHideOnSelect = true
                    chooseOrderCardAction.numCards = cardAction.cards.size
                    chooseOrderCardAction.cardName = "Black Market"
                    chooseOrderCardAction.cards = cardAction.cards
                    chooseOrderCardAction.buttonValue = "Done"
                    chooseOrderCardAction.instructions = "Click the cards in the order you want them to be on the bottom of the black market deck, starting with the top card and then click Done. (The last card you click will be the bottom card of the black market deck)"
                    game.setPlayerCardAction(player, chooseOrderCardAction)
                }
            }
            "Chancellor" -> if (yesNoAnswer == "yes") {
                player.discard.addAll(player.deck)
                player.deck.clear()
                game.addHistory(player.username, " added ", player.pronoun, " deck to ", player.pronoun, " discard")
            }
            "City Planner" -> if (yesNoAnswer == "yes") {
                player.subtractCoins(2)
                game.refreshAllPlayersCardsBought()

                if (KingdomUtil.uniqueCardList(player.getVictoryCards()).size == 1) {
                    val victoryCard = player.getVictoryCards()[0]
                    player.removeCardFromHand(victoryCard)
                    player.cityPlannerCards.add(victoryCard)
                    game.addHistory(player.username, " paid an extra $2 to set aside ", KingdomUtil.getArticleWithCardName(victoryCard))
                } else {
                    val chooseVictoryCardAction = CardAction(CardAction.TYPE_CHOOSE_CARDS)
                    chooseVictoryCardAction.deck = Deck.Proletariat
                    chooseVictoryCardAction.cardName = cardAction.cardName
                    chooseVictoryCardAction.cards.addAll(player.getVictoryCards())
                    chooseVictoryCardAction.numCards = 1
                    chooseVictoryCardAction.instructions = "Choose a victory card to set aside and then click Done."
                    chooseVictoryCardAction.buttonValue = "Done"
                    game.setPlayerCardAction(player, chooseVictoryCardAction)
                }
            }
            "Confirm Buy" -> if (yesNoAnswer == "yes") {
                game.removeProcessingClick(player)
                game.cardClicked(player, "supply", cardAction.cards[0], false)
            }
            "Confirm End Turn" -> if (yesNoAnswer == "yes") {
                game.removeProcessingClick(player)
                game.endPlayerTurn(player, false)
            }
            "Confirm Play Treasure Card" -> if (yesNoAnswer == "yes") {
                game.removeProcessingClick(player)
                game.cardClicked(player, "hand", cardAction.cards[0], false)
            }
            "Confirm Play Treasure Cards" -> if (yesNoAnswer == "yes") {
                game.removeProcessingClick(player)
                game.playAllTreasureCards(player, false)
            }
            "Duchess for Duchy" -> if (yesNoAnswer == "yes") {
                game.playerGainedCard(player, cardAction.cards[0])
            }
            "Enchanted Palace" -> if (yesNoAnswer == "yes") {
                player.drawCards(2)
                game.playerRevealedEnchantedPalace(player.userId)
            }
            "Fool's Gold" -> {
                if (yesNoAnswer == "yes") {
                    val foolsGoldCard = game.foolsGoldCard
                    game.addHistory(player.username, " revealed and trashed ", KingdomUtil.getCardWithBackgroundColor(foolsGoldCard!!))
                    player.removeCardFromHand(foolsGoldCard)
                    game.trashedCards.add(foolsGoldCard)
                    if (game.isCardInSupply(game.goldCard)) {
                        game.playerGainedCardToTopOfDeck(player, game.goldCard)
                    } else {
                        game.addHistory("There were no more Gold cards in the supply")
                    }
                }
                if (!player.isShowCardAction) {
                    game.playersWithCardActions.remove(player.userId)
                }
            }
            "Graverobber" -> {
                val affectedPlayer = playerMap[cardAction.playerId]!!
                game.addHistory("The Graverobber revealed ", KingdomUtil.getArticleWithCardName(cardAction.cards[0]), " from ", affectedPlayer.username, "'s discard pile")
                if (yesNoAnswer == "yes") {
                    game.addHistory(player.username, " chose to gain the revealed treasure card")
                    if (affectedPlayer.discard[cardAction.cardId].cardId == cardAction.cards[0].cardId) {
                        affectedPlayer.discard.removeAt(cardAction.cardId)
                    } else {
                        val error = GameError(GameError.GAME_ERROR, "Card in discard pile does not match expected Graverobber revealed card")
                        game.logError(error, false)
                    }
                    game.playerGainedCard(player, cardAction.cards[0], false)
                } else {
                    game.addHistory(player.username, " did not choose to gain the revealed treasure card")
                }
                if (!game.incompleteCard!!.extraCardActions.isEmpty()) {
                    val nextPlayerCardAction = game.incompleteCard!!.extraCardActions.peek()
                    //need to update index if card removed index was before next card's index
                    if (yesNoAnswer == "yes" && nextPlayerCardAction.playerId == cardAction.playerId && cardAction.cardId < nextPlayerCardAction.cardId) {
                        nextPlayerCardAction.cardId = nextPlayerCardAction.cardId - 1
                    }
                }
            }
            "Hamlet" -> if (yesNoAnswer == "yes") {
                val discardCardAction = CardAction(CardAction.TYPE_DISCARD_FROM_HAND)
                discardCardAction.deck = Deck.Cornucopia
                discardCardAction.cardName = cardAction.cardName
                discardCardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                discardCardAction.numCards = 1
                discardCardAction.instructions = "Select the card you want to discard and then click Done."
                discardCardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, discardCardAction)
            } else {
                game.addHistory(player.username, " chose not to discard a card for +1 Action")
                incompleteCard = SinglePlayerIncompleteCard(cardAction.cardName, game)
                game.addNextAction("discard for buy")
            }
            "Hamlet2" -> if (yesNoAnswer == "yes") {
                val discardCardAction = CardAction(CardAction.TYPE_DISCARD_FROM_HAND)
                discardCardAction.deck = Deck.Cornucopia
                discardCardAction.cardName = cardAction.cardName
                discardCardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                discardCardAction.numCards = 1
                discardCardAction.instructions = "Select the card you want to discard and then click Done."
                discardCardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, discardCardAction)
            } else {
                game.addHistory(player.username, " chose not to discard a card for +1 Buy")
            }
            "Horse Traders" -> if (yesNoAnswer == "yes") {
                game.addHistory(player.username, " set aside ", KingdomUtil.getWordWithBackgroundColor("Horse Traders", Card.ACTION_REACTION_COLOR))
                player.setAsideCardFromHand(game.horseTradersCard!!)
            }
            "Ill-Gotten Gains" -> if (yesNoAnswer == "yes") {
                if (game.isCardInSupply(game.copperCard)) {
                    game.playerGainedCardToHand(player, game.copperCard)
                }
            }
            "Library" -> {
                val card = cardAction.cards[0]
                when (yesNoAnswer) {
                    "yes" -> {
                        game.setAsideCards.add(card)
                        game.addHistory(player.username, " set aside ", KingdomUtil.getArticleWithCardName(card))
                    }
                    else -> player.addCardToHand(card)
                }
                var noTopCard = false
                while (player.hand.size < 7) {
                    val topCard = player.removeTopDeckCard()
                    if (topCard == null) {
                        noTopCard = true
                        break
                    }
                    if (topCard.isAction) {
                        val libraryCardAction = CardAction(CardAction.TYPE_YES_NO)
                        libraryCardAction.deck = Deck.Kingdom
                        libraryCardAction.cardName = "Library"
                        libraryCardAction.cards.add(topCard)
                        libraryCardAction.instructions = "Do you want to set aside this action card?"
                        game.setPlayerCardAction(player, libraryCardAction)
                        break
                    } else {
                        player.addCardToHand(topCard)
                    }
                }
                if (player.hand.size == 7 || noTopCard) {
                    for (setAsideCard in game.setAsideCards) {
                        player.addCardToDiscard(setAsideCard)
                        game.playerDiscardedCard(player, setAsideCard)
                    }
                    game.setAsideCards.clear()
                }
            }
            "Mining Village" -> if (yesNoAnswer == "yes") {
                val miningVillageCard = cardAction.cards[0]
                game.removePlayedCard(miningVillageCard)
                game.trashedCards.add(miningVillageCard)
                game.playerLostCard(player, miningVillageCard)
                player.addCoins(2)
                game.addHistory(player.username, " trashed a ", KingdomUtil.getWordWithBackgroundColor("Mining Village", Card.ACTION_COLOR), " to get +2 coins")
                game.refreshAllPlayersCardsPlayed()
            }
            "Museum Trash Cards" -> if (yesNoAnswer == "yes") {
                when {
                    player.museumCards.size == 4 -> {
                        game.trashedCards.addAll(player.museumCards)
                        player.museumCards.clear()
                        game.addHistory(player.username, " trashed 4 cards from ", player.pronoun, " Museum mat")
                        game.playerGainedCard(player, game.duchyCard)
                        when {
                            game.prizeCards.isEmpty() -> game.addHistory("There were no more prizes available")
                            game.prizeCards.size == 1 -> {
                                game.playerGainedCard(player, game.prizeCards[0], false)
                                game.prizeCards.clear()
                            }
                            else -> {
                                val choosePrizeCardAction = CardAction(CardAction.TYPE_GAIN_CARDS)
                                choosePrizeCardAction.deck = Deck.Fan
                                choosePrizeCardAction.cardName = "Museum"
                                choosePrizeCardAction.numCards = 1
                                choosePrizeCardAction.buttonValue = "Done"
                                choosePrizeCardAction.instructions = "Select one of the following prize cards to gain and then click Done."
                                choosePrizeCardAction.cards.addAll(game.prizeCards)
                                game.setPlayerCardAction(player, choosePrizeCardAction)
                            }
                        }
                    }
                    else -> {
                        val museumCardAction = CardAction(CardAction.TYPE_CHOOSE_CARDS)
                        museumCardAction.deck = Deck.Fan
                        museumCardAction.cardName = "Museum Trash Cards"
                        museumCardAction.cards.addAll(player.museumCards)
                        museumCardAction.numCards = 4
                        museumCardAction.instructions = "Select 4 cards to trash from your Museum mat and then click Done."
                        museumCardAction.buttonValue = "Done"
                        game.setPlayerCardAction(player, museumCardAction)
                    }
                }
            }
            "Orchard" -> if (yesNoAnswer == "yes") {
                player.subtractCoins(2)
                player.addFruitTokens(2)
                game.refreshAllPlayersCardsBought()
                game.addHistory(player.username, " paid an extra two coins to gain two fruit tokens")
            }
            "Pearl Diver" -> when (yesNoAnswer) {
                "yes" -> {
                    val bottomCard = player.deck.removeAt(player.deck.size - 1)
                    player.addCardToTopOfDeck(bottomCard)
                    game.addHistory(player.username, " put the bottom card of ", player.pronoun, " deck on top of ", player.pronoun, " deck")
                }
                else -> game.addHistory(player.username, " chose to keep the card on the bottom of ", player.pronoun, " deck")
            }
            "Royal Seal" -> {
                incompleteCard = SinglePlayerIncompleteCard(cardAction.cardName, game)
                val cardToGain = cardAction.cards[0]
                when (yesNoAnswer) {
                    "yes" -> {
                        game.addHistory(player.username, " used ", KingdomUtil.getWordWithBackgroundColor("Royal Seal", Card.TREASURE_COLOR), " to add the gained card to the top of ", player.pronoun, " deck")
                        game.moveGainedCard(player, cardToGain, "deck")
                    }
                    else -> when (cardAction.destination) {
                        "hand" -> game.playerGainedCardToHand(player, cardToGain)
                        "discard" -> game.playerGainedCard(player, cardToGain)
                    }
                }
                if (player.buys == 0 && !player.isComputer && !player.isShowCardAction && player.extraCardActions.isEmpty() && !game.hasUnfinishedGainCardActions()) {
                    incompleteCard.isEndTurn = true
                }
            }
            "Scrying Pool" -> {
                val cardActionPlayer = playerMap[cardAction.playerId]!!
                var topDeckCard = cardActionPlayer.lookAtTopDeckCard()
                when {
                    topDeckCard != null -> {
                        game.addHistory("The top card of ", cardActionPlayer.username, "'s deck was ", KingdomUtil.getArticleWithCardName(topDeckCard))
                        when (yesNoAnswer) {
                            "yes" -> {
                                cardActionPlayer.deck.removeAt(0)
                                cardActionPlayer.addCardToDiscard(topDeckCard)
                                game.playerDiscardedCard(cardActionPlayer, topDeckCard)
                                game.refreshDiscard(cardActionPlayer)
                                game.addHistory(game.currentPlayer!!.username, " decided to discard the card")
                            }
                            else -> game.addHistory(game.currentPlayer!!.username, " decided to keep the card on top of ", player.pronoun, " deck")
                        }
                    }
                    else -> game.addHistory(game.currentPlayer!!.username, " did not have a card to draw")
                }
                if (game.incompleteCard!!.extraCardActions.isEmpty()) {
                    val currentPlayer = game.currentPlayer
                    val revealedCards = ArrayList<Card>()
                    var foundNonActionCard = false
                    while (!foundNonActionCard) {
                        topDeckCard = currentPlayer!!.removeTopDeckCard()
                        if (topDeckCard == null) {
                            break
                        }
                        revealedCards.add(topDeckCard)
                        if (!topDeckCard.isAction) {
                            foundNonActionCard = true
                        }
                        currentPlayer.addCardToHand(topDeckCard)
                    }
                    if (!revealedCards.isEmpty()) {
                        game.addHistory(currentPlayer!!.username, " revealed ", KingdomUtil.groupCards(revealedCards, true))
                    }
                }
            }
            "Secret Chamber" -> if (yesNoAnswer == "yes") {
                game.addHistory(player.username, " is using a ", KingdomUtil.getWordWithBackgroundColor("Secret Chamber", Card.ACTION_REACTION_COLOR))
                player.drawCards(2)
                val secretChamberAction = CardAction(CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK)
                secretChamberAction.deck = Deck.Intrigue
                secretChamberAction.cardName = "Secret Chamber"
                secretChamberAction.cards.addAll(player.hand)
                secretChamberAction.buttonValue = "Done"
                secretChamberAction.numCards = 2
                secretChamberAction.instructions = "Select two cards from your hand to put on top of your deck."
                game.setPlayerCardAction(player, secretChamberAction)
            }
            "Shepherd" -> if (yesNoAnswer == "yes") {
                player.subtractCoins(2)
                player.addCattleTokens(2)
                game.refreshAllPlayersCardsBought()
                game.addHistory(player.username, " paid an extra $2 to gain 2 cattle tokens")
            }
            "Spy" -> {
                val cardActionPlayer = playerMap[cardAction.playerId]!!
                val topDeckCard = cardActionPlayer.lookAtTopDeckCard()
                if (topDeckCard != null) {
                    game.addHistory("The top card of ", cardActionPlayer.username, "'s deck was ", KingdomUtil.getArticleWithCardName(topDeckCard))
                    when (yesNoAnswer) {
                        "yes" -> {
                            cardActionPlayer.deck.removeAt(0)
                            cardActionPlayer.addCardToDiscard(topDeckCard)
                            game.playerDiscardedCard(cardActionPlayer, topDeckCard)
                            game.refreshDiscard(cardActionPlayer)
                            game.addHistory(game.currentPlayer!!.username, " decided to discard the card")
                        }
                        else -> game.addHistory(game.currentPlayer!!.username, " decided to keep the card on top of ", cardActionPlayer.pronoun, " deck")
                    }
                } else {
                    game.addHistory(game.currentPlayer!!.username, " did not have a card to draw")
                }
            }
            "Squatter" -> {
                val squatterCard = cardAction.associatedCard
                if (yesNoAnswer == "yes") {
                    game.addHistory(player.username, " returned ", KingdomUtil.getCardWithBackgroundColor(squatterCard!!), " to the supply")
                    cardAction.isGainCardAfterBuyAction = false

                    var playerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                    while (playerIndex != game.currentPlayerIndex) {
                        val nextPlayer = game.players[playerIndex]
                        if (game.isCardInSupply(squatterCard)) {
                            game.playerGainedCard(nextPlayer, game.cardMap[squatterCard.cardId]!!)
                            game.refreshDiscard(nextPlayer)
                        }
                        playerIndex = game.calculateNextPlayerIndex(playerIndex)
                    }
                }
            }
            "Tinker" -> {
                val cardToGain = cardAction.cards[0]
                when (yesNoAnswer) {
                    "yes" -> {
                        game.addHistory(player.username, " put ", KingdomUtil.getArticleWithCardName(cardToGain), " under ", player.pronoun, " ", KingdomUtil.getWordWithBackgroundColor("Tinker", Card.ACTION_DURATION_COLOR))
                        player.tinkerCards.add(cardToGain)
                        game.moveGainedCard(player, cardToGain, "tinker")
                    }
                    else -> when (cardAction.destination) {
                        "hand" -> game.playerGainedCardToHand(player, cardToGain)
                        "deck" -> game.playerGainedCardToTopOfDeck(player, cardToGain)
                        "discard" -> game.playerGainedCard(player, cardToGain)
                    }
                }
            }
            "Tournament" -> if (yesNoAnswer == "yes") {
                when {
                    player.userId == game.currentPlayerId -> {
                        player.discardCardFromHand(game.provinceCard)
                        game.addHistory(player.username, " revealed and discarded a ", KingdomUtil.getCardWithBackgroundColor(game.provinceCard))
                        val chooseType = CardAction(CardAction.TYPE_CHOICES)
                        chooseType.deck = Deck.Cornucopia
                        chooseType.cardName = cardAction.cardName
                        chooseType.instructions = "Available Prizes: " + game.prizeCardsString + ". Do you want to gain a Prize or a Duchy?"
                        chooseType.choices.add(CardActionChoice("Prize", "prize"))
                        chooseType.choices.add(CardActionChoice("Duchy", "duchy"))
                        game.setPlayerCardAction(player, chooseType)
                    }
                    else -> {
                        game.isGainTournamentBonus = false
                        game.addHistory(player.username, " revealed a ", KingdomUtil.getCardWithBackgroundColor(game.provinceCard))
                    }
                }
            }
            "Tunnel" -> {
                if (yesNoAnswer == "yes") {
                    game.addHistory(player.username, " revealed a ", KingdomUtil.getCardWithBackgroundColor(cardAction.associatedCard!!))
                    if (game.isCardInSupply(game.goldCard)) {
                        game.playerGainedCard(player, game.goldCard)
                    } else {
                        game.addHistory("There were no more Gold cards in the supply")
                    }
                }
                game.finishTunnelCardAction(player)
            }
            "Vault" -> when (yesNoAnswer) {
                "yes" -> when {
                    player.hand.size == 1 -> {
                        for (c in player.hand) {
                            game.playerDiscardedCard(player, c)
                        }
                        player.discardHand()
                        game.addHistory(player.username, " discarded the last card from ", player.pronoun, " hand")
                    }
                    else -> {
                        val discardCardsAction = CardAction(CardAction.TYPE_DISCARD_FROM_HAND)
                        discardCardsAction.deck = Deck.Prosperity
                        discardCardsAction.cardName = "Vault2"
                        discardCardsAction.cards.addAll(player.hand)
                        discardCardsAction.buttonValue = "Done"
                        discardCardsAction.numCards = 2
                        discardCardsAction.instructions = "Select two cards from your hand to discard."
                        game.setPlayerCardAction(player, discardCardsAction)
                    }
                }
                else -> game.addHistory(player.username, " chose not to discard")
            }
            "Walled Village" -> {
                incompleteCard = SinglePlayerIncompleteCard(cardAction.cardName, game)
                if (yesNoAnswer == "yes") {
                    val walledVillage = cardAction.cards[0]
                    game.removePlayedCard(walledVillage)
                    player.addCardToTopOfDeck(walledVillage)
                    game.addHistory(player.username, " added ", KingdomUtil.getCardWithBackgroundColor(walledVillage), " to the top of ", player.pronoun, " deck")
                }
                incompleteCard.isEndTurn = true
            }
        }

        return incompleteCard
    }
}
