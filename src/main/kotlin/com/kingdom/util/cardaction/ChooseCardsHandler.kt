package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object ChooseCardsHandler {
    fun handleCardAction(game: Game, player: Player, oldCardAction: OldCardAction, selectedCardNames: List<String>): IncompleteCard? {

        val cardMap = game.cardMap
        val playerMap = game.playerMap
        val incompleteCard: IncompleteCard? = null
        val selectedCard = cardMap[selectedCardNames[0]]!!

        when (oldCardAction.cardName) {
            "Ambassador" -> {
                val addToSupplyCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_UP_TO).apply {
                    deck = Deck.Seaside
                    instructions = "Select the cards that you want to return to the supply and then click Done."
                    buttonValue = "Done"
                    cardName = "Ambassador"
                }
                for (card in player.hand) {
                    if (addToSupplyCardAction.cards.size == 2) {
                        break
                    }
                    if (card.name == selectedCard.name) {
                        addToSupplyCardAction.cards.add(card)
                    }
                }
                addToSupplyCardAction.numCards = addToSupplyCardAction.cards.size
                game.setPlayerCardAction(player, addToSupplyCardAction)
            }
            "Baptistry" -> {
                game.addHistory(player.username, " chose ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " for Baptistry")

                var hasMoreCards = true
                val revealedCards = ArrayList<Card>()
                while (hasMoreCards && revealedCards.size < 5) {
                    val c = player.removeTopDeckCard()
                    if (c == null) {
                        hasMoreCards = false
                    } else {
                        revealedCards.add(c)
                    }
                }
                if (!revealedCards.isEmpty()) {
                    game.addHistory("Baptistry revealed ", KingdomUtil.groupCards(revealedCards, true))
                    var cardsTrashed = 0
                    for (revealedCard in revealedCards) {
                        if (revealedCard.name == selectedCard.name) {
                            game.trashedCards.add(revealedCard)
                            game.playerLostCard(player, revealedCard)
                            cardsTrashed++
                        } else {
                            player.addCardToDiscard(revealedCard)
                            game.playerDiscardedCard(player, revealedCard)
                        }
                    }
                    if (cardsTrashed > 0) {
                        game.addHistory(player.username, " trashed ", KingdomUtil.getPlural(cardsTrashed, "card"), " and removed 1 sin")
                        player.addSins(-1)
                        game.refreshAllPlayersPlayers()
                    }
                } else {
                    game.addHistory(player.username, " did not have any cards to reveal")
                }
            }
            "Bilkis" -> {
                game.playerGainedCardToTopOfDeck(player, selectedCard)
            }
            "Bridge Troll" -> {
                game.addTrollToken(selectedCard.name)
                game.addHistory(player.username, " added a troll token to the ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " card")
            }
            "Catacombs" -> {
                game.playerGainedCardToHand(player, selectedCard, false)
                player.discard.remove(selectedCard)
                game.refreshHandArea(player)
            }
            "City Planner" -> {
                player.removeCardFromHand(selectedCard)
                player.cityPlannerCards.add(selectedCard)
                game.addHistory(player.username, " paid an extra $2 to set aside ", KingdomUtil.getArticleWithCardName(selectedCard))
            }
            "Contraband" -> {
                game.addHistory(game.currentPlayer!!.username, " can't buy ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " this turn")
                game.contrabandCards.add(selectedCard)
            }
            "Edict" -> {
                game.edictCards.add(selectedCard)
                player.edictCards.add(selectedCard)
                game.addHistory(player.username, " played an Edict on ", KingdomUtil.getCardWithBackgroundColor(selectedCard))
            }
            "Embargo" -> {
                game.addEmbargoToken(selectedCard.name)
                game.addHistory(player.username, " added an embargo token to the ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " card")
            }
            "Envoy" -> {
                val currentPlayer = game.currentPlayer!!
                currentPlayer.addCardToDiscard(selectedCard)
                game.playerDiscardedCard(currentPlayer, selectedCard)
                oldCardAction.cards.remove(selectedCard)
                for (card in oldCardAction.cards) {
                    currentPlayer.addCardToHand(card)
                }
                game.refreshHandArea(currentPlayer)
                game.refreshCardsBought(currentPlayer)
                game.addHistory(player.username, " chose to discard ", currentPlayer.username, "'s ", KingdomUtil.getCardWithBackgroundColor(selectedCard))
            }
            "Golem" -> {
                val firstAction = cardMap[selectedCardNames[0]]!!
                val secondAction: Card
                secondAction = if (oldCardAction.cards[0].name == firstAction.name) {
                    oldCardAction.cards[1]
                } else {
                    oldCardAction.cards[0]
                }
                game.golemActions.push(secondAction)
                game.golemActions.push(firstAction)
                game.playGolemActionCard(player)
            }
            "Graverobber" -> {
                val affectedPlayer = playerMap[oldCardAction.playerId]!!
                game.addHistory("The Graverobber revealed ", KingdomUtil.getArticleWithCardName(oldCardAction.cards[0]), " from ", affectedPlayer.username, "'s discard pile")
            }
            "Haven" -> {
                player.removeCardFromHand(selectedCard)
                player.havenCards.add(selectedCard)
            }
            "Hooligans" -> {
                player.removeCardFromHand(selectedCard)
                val chooseDestinationCardAction = OldCardAction(OldCardAction.TYPE_CHOICES).apply {
                    deck = Deck.Proletariat
                    cardName = oldCardAction.cardName
                    associatedCard = selectedCard
                    playerId = player.userId
                    cards.add(selectedCard)
                    instructions = player.username + " revealed " + KingdomUtil.getArticleWithCardName(selectedCard) + ". Do you want to discard it, or put it on top of " + player.pronoun + " deck?"
                    choices.add(CardActionChoice("Discard", "discard"))
                    choices.add(CardActionChoice("Top of deck", "deck"))
                }
                game.setPlayerCardAction(game.currentPlayer!!, chooseDestinationCardAction)
            }
            "Island" -> {
                player.removeCardFromHand(selectedCard)
                player.islandCards.add(selectedCard)
            }
            "Masquerade" -> {
                player.removeCardFromHand(selectedCard)
                game.playerLostCard(player, selectedCard)
                game.refreshHand(player)
                game.masqueradeCards[player.userId] = selectedCard
            }
            "Mint" -> {
                game.addHistory(player.username, " minted ", KingdomUtil.getArticleWithCardName(selectedCard))
                game.playerGainedCard(player, selectedCard)
            }
            "Museum Trash Cards" -> {
                for (selectedCardName in selectedCardNames) {
                    val card = cardMap[selectedCardName]!!
                    game.trashedCards.add(card)
                    player.museumCards.remove(card)
                }
                game.playerGainedCard(player, game.duchyCard)
                when {
                    game.prizeCards.isEmpty() -> game.addHistory("There were no more prizes available")
                    game.prizeCards.size == 1 -> {
                        game.playerGainedCard(player, game.prizeCards[0], false)
                        game.prizeCards.clear()
                    }
                    else -> {
                        val choosePrizeCardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS).apply {
                            deck = Deck.Fan
                            cardName = "Museum"
                            numCards = 1
                            buttonValue = "Done"
                            instructions = "Select one of the following prize cards to gain and then click Done."
                            cards.addAll(game.prizeCards)
                        }
                        game.setPlayerCardAction(player, choosePrizeCardAction)
                    }
                }
            }
            "Pirate Ship" -> {
                val currentPlayer = game.currentPlayer
                val affectedPlayer = playerMap[oldCardAction.playerId]!!
                var selectedCardName = ""
                if (selectedCardNames.isNotEmpty()) {
                    selectedCardName = selectedCardNames[0]
                }
                var foundSelectedCard = false
                game.addHistory("The top two cards from ", affectedPlayer.username, "'s deck were ", oldCardAction.cards[0].name, " and ", oldCardAction.cards[1].name)
                for (card in oldCardAction.cards) {
                    card.isDisableSelect = false
                    if (!foundSelectedCard && card.name == selectedCardName) {
                        foundSelectedCard = true
                        game.trashedTreasureCards.add(card)
                        game.trashedCards.add(card)
                        game.addHistory(currentPlayer!!.username, " trashed ", affectedPlayer.username, "'s ", card.name)
                    } else {
                        affectedPlayer.addCardToDiscard(card)
                        game.playerDiscardedCard(affectedPlayer, card)
                        game.refreshDiscard(affectedPlayer)
                    }
                }
                if (game.incompleteCard!!.extraOldCardActions.isEmpty()) {
                    if (game.trashedTreasureCards.size > 0) {
                        currentPlayer!!.addPirateShipCoin()
                        game.addHistory(currentPlayer.username, " gained a Pirate Ship Coin, and now has ", KingdomUtil.getPlural(currentPlayer.pirateShipCoins, "Coin"))
                    } else {
                        game.addHistory(currentPlayer!!.username, " did not gain a Pirate Ship Coin")
                    }
                    game.trashedTreasureCards.clear()
                }
            }
            "Quest" -> {
                oldCardAction.associatedCard!!.associatedCards.add(selectedCard)
                game.addHistory(player.username, " named ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " for ", player.pronoun, " ", KingdomUtil.getCardWithBackgroundColor(oldCardAction.associatedCard!!))
            }
            "Setup Leaders" -> for (selectedCardName in selectedCardNames) {
                player.leaders.add(Card(selectedCard))
            }
            "Swindler" -> {
                if (game.supply[selectedCard.name] == 0) {
                    val chooseAgainCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS).apply {
                        deck = Deck.Intrigue
                        numCards = 1
                        buttonValue = "Done"
                        cardName = "Swindler"
                        instructions = "The card you selected is no longer in the supply. Select one of the following cards and then click Done."
                        playerId = oldCardAction.playerId
                    }
                    val card = oldCardAction.cards[0]
                    val cost = card.cost
                    game.supplyMap.values
                            .filterTo (chooseAgainCardAction.cards) { game.getCardCost(it) == cost && it.costIncludesPotion == card.costIncludesPotion && game.supply[it.name]!! > 0 }
                    if (chooseAgainCardAction.cards.size > 0) {
                        game.setPlayerCardAction(game.currentPlayer!!, chooseAgainCardAction)
                    } else {
                        game.addHistory(game.players[oldCardAction.playerId].username, " did not have any cards in the supply to gain")
                    }
                } else {
                    val cardActionPlayer = playerMap[oldCardAction.playerId]!!
                    game.playerGainedCard(cardActionPlayer, selectedCard)
                    game.refreshDiscard(cardActionPlayer)
                    game.addHistory("The Swindler gave ", cardActionPlayer.username, " ", KingdomUtil.getArticleWithCardName(selectedCard))
                }
            }
            "Thief" -> {
                val currentPlayer = game.currentPlayer!!
                val affectedPlayer = playerMap[oldCardAction.playerId]!!
                var selectedCardName = ""
                if (selectedCardNames.isNotEmpty()) {
                    selectedCardName = selectedCardNames[0]
                }
                var foundSelectedCard = false
                when {
                    oldCardAction.cards.size == 2 -> game.addHistory("The top two cards from ", affectedPlayer.username, "'s deck were ", KingdomUtil.getCardWithBackgroundColor(oldCardAction.cards[0]), " and ", KingdomUtil.getCardWithBackgroundColor(oldCardAction.cards[1]))
                    oldCardAction.cards.size == 1 -> game.addHistory("The top card from ", affectedPlayer.username, "'s deck was ", KingdomUtil.getCardWithBackgroundColor(oldCardAction.cards[0]))
                    else -> game.addHistory(affectedPlayer.username, " did not have any cards in ", affectedPlayer.pronoun, " deck")
                }
                for (card in oldCardAction.cards) {
                    card.isDisableSelect = false
                    if (!foundSelectedCard && card.name == selectedCardName) {
                        foundSelectedCard = true
                        game.trashedTreasureCards.add(card)
                        game.addHistory(currentPlayer.username, " trashed ", affectedPlayer.username, "'s ", KingdomUtil.getCardWithBackgroundColor(card))
                    } else {
                        affectedPlayer.addCardToDiscard(card)
                        game.playerDiscardedCard(affectedPlayer, card)
                        game.refreshDiscard(affectedPlayer)
                    }
                }
                if (game.incompleteCard!!.extraOldCardActions.isEmpty()) {
                    val selectFromTrashedCardsAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_UP_TO).apply {
                        deck = Deck.Kingdom
                        cardName = "Thief"
                        cards.addAll(game.trashedTreasureCards)
                        numCards = game.trashedTreasureCards.size
                    }
                    selectFromTrashedCardsAction.instructions = if (game.trashedTreasureCards.isNotEmpty()) {
                        "Select any of the trashed treasure cards you want to gain and then click Done."
                    } else {
                        "There were no treasure cards trashed. Click Done."
                    }
                    selectFromTrashedCardsAction.buttonValue = "Done"
                    game.setPlayerCardAction(currentPlayer, selectFromTrashedCardsAction)
                    game.trashedTreasureCards.clear()
                }
            }
            "Throne Room" -> {
                val actionCard = player.getCardFromHandById(selectedCardNames[0])!!
                val cardCopy: Card
                if (game.isCheckQuest && actionCard.name == "Quest") {
                    cardCopy = Card(actionCard)
                    game.copiedPlayedCard = true
                } else {
                    cardCopy = actionCard
                }
                val action1 = RepeatedAction(cardCopy)
                action1.isFirstAction = true
                val action2 = RepeatedAction(cardCopy)
                game.repeatedActions.push(action2)
                game.repeatedActions.push(action1)
                if (actionCard.isDuration) {
                    game.durationCardsPlayed.add(game.throneRoomCard!!)
                }
                game.addHistory(player.username, " throne roomed ", KingdomUtil.getArticleWithCardName(actionCard))
                game.playRepeatedAction(player, true)
            }
            "Trainee" -> {
                val combinedCost = game.getCardCost(selectedCard) + game.getCardCost(oldCardAction.associatedCard!!)
                player.removeCardFromHand(selectedCard)
                game.removePlayedCard(oldCardAction.associatedCard!!)
                game.addToSupply(selectedCard.name)
                game.addToSupply(oldCardAction.associatedCard!!.name)
                game.refreshAllPlayersCardsPlayed()
                game.refreshAllPlayersSupply()
                game.addHistory(player.username, " returned ", KingdomUtil.getCardWithBackgroundColor(oldCardAction.associatedCard!!), " and ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " to the supply")
                val gainCardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                    deck = Deck.Proletariat
                    cardName = oldCardAction.cardName
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards to gain and then click Done."
                }
                game.supplyMap.values
                        .filterTo (gainCardAction.cards) { it.isAction && game.getCardCost(it) <= combinedCost && (!it.costIncludesPotion || selectedCard.costIncludesPotion || oldCardAction.associatedCard!!.costIncludesPotion) && game.isCardInSupply(it) }
                if (gainCardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, gainCardAction)
                }
            }
            "Wishing Well" -> {
                val topDeckCard = player.lookAtTopDeckCard()
                if (topDeckCard != null) {
                    val guessedRight = selectedCard.name == topDeckCard.name
                    if (guessedRight) {
                        player.drawCards(1)
                        game.addHistory(player.username, " correctly guessed the top card of ", player.pronoun, " deck was ", KingdomUtil.getArticleWithCardName(selectedCard))
                    } else {
                        game.addHistory(player.username, " guessed the top card of ", player.pronoun, " deck was ", KingdomUtil.getArticleWithCardName(selectedCard), ", but it was ", KingdomUtil.getArticleWithCardName(topDeckCard))
                    }
                }
            }
        }

        return incompleteCard
    }
}
