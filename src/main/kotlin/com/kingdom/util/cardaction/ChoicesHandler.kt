package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.*
import com.kingdom.util.KingdomUtil
import java.util.*

object ChoicesHandler {
    fun handleCardAction(game: OldGame, player: OldPlayer, oldCardAction: OldCardAction, choice: String): IncompleteCard? {

        val players = game.players
        var incompleteCard: IncompleteCard? = null

        when (oldCardAction.cardName) {
            "Archbishop" -> when (choice) {
                "actions" -> {
                    player.addActions(2)
                    game.addHistory(player.username, " chose +2 Actions")
                    game.refreshAllPlayersCardsPlayed()
                }
                "remove" -> {
                    player.addSins(-1)
                    game.refreshAllPlayersPlayers()
                    game.addHistory(player.username, " chose to remove 1 sin")
                    game.refreshAllPlayersCardsPlayed()
                }
                "sins" -> {
                    game.addHistory(player.username, " chose for all other players to gain 1 sin")
                    for (otherPlayer in game.players) {
                        if (otherPlayer.userId != game.currentPlayerId) {
                            otherPlayer.addSins(1)
                            game.refreshHandArea(otherPlayer)
                        }
                    }
                    game.refreshAllPlayersPlayers()
                }
            }
            "Archivist" -> when (choice) {
                "draw" -> {
                    while (player.hand.size < 6) {
                        val topCard = player.removeTopDeckCard() ?: break
                        player.addCardToHand(topCard)
                    }
                    game.refreshHand(player)
                    game.addHistory(player.username, " chose to draw until 6 cards in hand")
                }
                "discard" -> {
                    player.addCoins(1)
                    game.addHistory(player.username, " chose +$1 and discard")
                    if (!player.hand.isEmpty()) {
                        val discardCardAction = OldCardAction(OldCardAction.TYPE_DISCARD_AT_LEAST_FROM_HAND)
                        discardCardAction.deck = Deck.Fan
                        discardCardAction.cardName = oldCardAction.cardName
                        discardCardAction.cards = player.hand
                        discardCardAction.numCards = 1
                        discardCardAction.instructions = "Select 1 or more cards to discard from your hand and then click Done."
                        discardCardAction.buttonValue = "Done"
                        game.setPlayerCardAction(player, discardCardAction)
                    }
                }
            }
            "Bell Tower" -> when (choice) {
                "before" -> {
                    player.drawCards(2)
                    game.addHistory(player.username, " revealed " + player.pronoun + " " + KingdomUtil.getWordWithBackgroundColor("Bell Tower", Card.ACTION_REACTION_COLOR) + " to gain +2 Cards before the attack")
                }
                "after" -> game.playersWaitingForBellTowerBonus.add(player)
            }
            "Cattle Farm" -> when (choice) {
                "discard" -> {
                    game.addHistory(player.username, " discarded the top card of ", player.pronoun, " deck")
                    player.addCardToDiscard(oldCardAction.associatedCard!!)
                    game.playerDiscardedCard(player, oldCardAction.associatedCard!!)
                }
                "back" -> {
                    game.addHistory(player.username, " put back the top card of ", player.pronoun, " deck")
                    player.addCardToTopOfDeck(oldCardAction.associatedCard!!)
                }
            }
            "Choose Reaction" -> {
                val cardToGain = oldCardAction.associatedCard
                val reaction = cardToGain!!.gainOldCardActions.remove(choice)
                if (reaction != null) {
                    game.setPlayerCardAction(player, reaction)
                }
            }
            "Develop" -> {
                val gainCardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
                gainCardAction.deck = Deck.Hinterlands
                gainCardAction.cardName = oldCardAction.cardName
                gainCardAction.buttonValue = "Done"
                gainCardAction.numCards = 1
                gainCardAction.associatedCard = oldCardAction.associatedCard
                gainCardAction.instructions = "Select one of the following cards to gain and then click Done."

                var cost = game.getCardCost(gainCardAction.associatedCard!!)
                when (choice) {
                    "more" -> {
                        cost += 1
                        gainCardAction.phase = 1
                    }
                    "less" -> {
                        cost -= 1
                        gainCardAction.phase = 2
                    }
                }
                val cards = game.supplyMap.values
                        .filter { game.getCardCost(it) == cost && oldCardAction.associatedCard!!.costIncludesPotion == it.costIncludesPotion && game.isCardInSupply(it) }
                        .toMutableList()

                gainCardAction.cards = cards
                game.setPlayerCardAction(player, gainCardAction)
            }
            "Duchess" -> when (choice) {
                "discard" -> {
                    game.addHistory(player.username, " discarded the top card of ", player.pronoun, " deck")
                    player.addCardToDiscard(oldCardAction.associatedCard!!)
                    game.playerDiscardedCard(player, oldCardAction.associatedCard!!)
                }
                "back" -> {
                    game.addHistory(player.username, " put back the top card of ", player.pronoun, " deck")
                    player.addCardToTopOfDeck(oldCardAction.associatedCard!!)
                }
            }
            "Explorer" -> when (choice) {
                "gold" -> if (game.isCardInSupply(Gold.NAME)) {
                    game.playerGainedCardToHand(player, game.goldCard)
                    game.refreshHand(player)
                }
                "silver" -> if (game.isCardInSupply(Silver.NAME)) {
                    game.playerGainedCardToHand(player, game.silverCard)
                    game.refreshHand(player)
                }
            }
            "Governor" -> when (choice) {
                "cards" -> {
                    game.addHistory(player.username, " chose to gain cards")
                    for (p in game.players) {
                        when {
                            game.isCurrentPlayer(p) -> p.drawCards(3)
                            else -> p.drawCards(1)
                        }
                    }
                    game.refreshAllPlayersHand()
                }
                "money" -> {
                    game.addHistory(player.username, " chose Silver and Gold")
                    if (game.supply[Gold.NAME]!! > 0) {
                        game.playerGainedCard(player, game.goldCard)
                    }
                    var playerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                    while (playerIndex != game.currentPlayerIndex) {
                        val nextPlayer = players[playerIndex]
                        if (game.supply[Silver.NAME]!! > 0) {
                            game.playerGainedCard(nextPlayer, game.silverCard)
                        }
                        playerIndex = game.calculateNextPlayerIndex(playerIndex)
                    }
                    game.refreshAllPlayersDiscard()
                }
                "trash" -> {
                    game.addHistory(player.username, " chose to trash and gain cards")
                    //todo do in player order
                    incompleteCard = MultiPlayerIncompleteCard(oldCardAction.cardName, game, true)
                    for (p in game.players) {
                        if (!p.hand.isEmpty()) {
                            var addToCost = 1
                            if (game.isCurrentPlayer(p)) {
                                addToCost = 2
                            }
                            val trashCardAction = OldCardAction(OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND).apply {
                                deck = Deck.Promo
                                cardName = oldCardAction.cardName
                                cards = KingdomUtil.uniqueCardList(p.hand)
                                numCards = 1
                                instructions = "Select a card to trash in order to gain a card costing exactly " + KingdomUtil.getPlural(addToCost, "coin") + "  more and then click Done, or just click Done if you don't want to trash a card."
                                buttonValue = "Done"
                            }
                            game.setPlayerCardAction(p, trashCardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(p.userId)
                        }
                    }
                    incompleteCard.allActionsSet()
                }
            }
            "Hooligans" -> {
                val affectedPlayer = game.playerMap[oldCardAction.playerId]!!
                when (choice) {
                    "discard" -> {
                        game.addHistory(player.username, " discarded ", affectedPlayer.username, "'s ", KingdomUtil.getCardWithBackgroundColor(oldCardAction.associatedCard!!))
                        affectedPlayer.addCardToDiscard(oldCardAction.associatedCard!!)
                        game.playerDiscardedCard(affectedPlayer, oldCardAction.associatedCard!!)
                    }
                    "deck" -> {
                        game.addHistory(player.username, " put the selected card on top of ", affectedPlayer.username, "'s deck")
                        affectedPlayer.addCardToTopOfDeck(oldCardAction.associatedCard!!)
                    }
                }
            }
            "Jack of all Trades" -> {
                when (choice) {
                    "discard" -> {
                        game.addHistory(player.username, " discarded the top card of ", player.pronoun, " deck")
                        player.addCardToDiscard(oldCardAction.associatedCard!!)
                        game.playerDiscardedCard(player, oldCardAction.associatedCard!!)
                    }
                    "back" -> {
                        game.addHistory(player.username, " put back the top card of ", player.pronoun, " deck")
                        player.addCardToTopOfDeck(oldCardAction.associatedCard!!)
                    }
                }
                while (player.hand.size < 5) {
                    val topCard = player.removeTopDeckCard() ?: break
                    player.addCardToHand(topCard)
                }

                game.refreshHand(player)

                val cards = player.hand.filterNot { it.isTreasure }.toMutableList()
                if (!cards.isEmpty()) {
                    val trashCardAction = OldCardAction(OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND).apply {
                        deck = Deck.Hinterlands
                        cardName = oldCardAction.cardName
                        numCards = 1
                        cards.addAll(KingdomUtil.uniqueCardList(cards))
                        instructions = "Select a card to trash from your hand and then click Done, or just click Done if you don't want to trash a card."
                        buttonValue = "Done"
                    }
                    game.setPlayerCardAction(player, trashCardAction)
                }
            }
            "Jester" -> {
                val cardToGain = oldCardAction.cards[0]
                when (choice) {
                    "me" -> when {
                        game.isCardInSupply(cardToGain) -> game.playerGainedCard(player, cardToGain)
                        else -> game.addHistory("The supply did not have ", KingdomUtil.getArticleWithCardName(cardToGain))
                    }
                    "them" -> {
                        val affectedPlayer = game.playerMap[oldCardAction.playerId]!!
                        if (game.isCardInSupply(cardToGain)) {
                            game.playerGainedCard(affectedPlayer, cardToGain)
                            game.refreshDiscard(affectedPlayer)
                        } else {
                            game.addHistory("The supply did not have ", KingdomUtil.getArticleWithCardName(cardToGain))
                        }
                    }
                }
            }
            "Loan" -> {
                val treasureCard = oldCardAction.cards[0]
                when (choice) {
                    "discard" -> {
                        player.addCardToDiscard(treasureCard)
                        game.addHistory(player.username, " discarded ", KingdomUtil.getArticleWithCardName(treasureCard))
                    }
                    "trash" -> {
                        game.trashedCards.add(treasureCard)
                        game.addHistory(player.username, " trashed ", KingdomUtil.getArticleWithCardName(treasureCard))
                        game.playerLostCard(player, treasureCard)
                    }
                }
            }
            "Lost Village 1" -> {
                when (choice) {
                    "actions" -> {
                        player.addActions(2)
                        game.addHistory(player.username, " chose +2 Actions")
                    }
                    "draw" -> {
                        player.addActions(1)
                        game.addHistory(player.username, " chose +1 Action and set aside cards until you choose to draw one.")
                        val nextCardAction = OldCardAction(OldCardAction.TYPE_CHOICES).apply {
                            deck = Deck.FairyTale
                            cardName = "Lost Village"
                            instructions = "Draw or set aside top card?"
                            choices.add(CardActionChoice("Draw", "draw"))
                            choices.add(CardActionChoice("Set Aside", "aside"))
                        }
                        game.setPlayerCardAction(player, nextCardAction)
                    }
                }
                game.refreshAllPlayersCardsPlayed()
            }
            "Lost Village" -> if (choice == "aside") {
                val card = player.removeTopDeckCard()
                when {
                    card != null -> {
                        game.addHistory(player.username, " set aside ", KingdomUtil.getArticleWithCardName(card))
                        game.setAsideCards.add(card)
                        val nextCardAction = OldCardAction(OldCardAction.TYPE_CHOICES).apply {
                            deck = Deck.FairyTale
                            cardName = oldCardAction.cardName
                            instructions = "Draw or set aside top card?"
                            choices.add(CardActionChoice("Draw", "draw"))
                            choices.add(CardActionChoice("Set Aside", "aside"))
                        }
                        game.setPlayerCardAction(player, nextCardAction)
                    }
                    else -> {
                        game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("Your deck is empty."))
                        game.addHistory(player.username, "'s deck is empty")
                        for (setAsideCard in game.setAsideCards) {
                            player.addCardToDiscard(setAsideCard)
                        }
                        game.setAsideCards.clear()
                    }
                }
            } else if (choice == "draw") {
                player.drawCards(1)
                game.addHistory(player.username, " drew the top card of ", player.pronoun, " deck")
                for (setAsideCard in game.setAsideCards) {
                    player.addCardToDiscard(setAsideCard)
                }
                game.setAsideCards.clear()
            }
            "Magic Beans" -> {
                val card = oldCardAction.cards[0]
                when (choice) {
                    "trash" -> {
                        game.addHistory(player.username, " chose to trash ", KingdomUtil.getCardWithBackgroundColor(card))
                        game.removePlayedCard(card)
                        game.trashedCards.add(card)
                        game.playerLostCard(player, card)
                    }
                    "supply" -> {
                        game.addHistory(player.username, " chose to return ", KingdomUtil.getCardWithBackgroundColor(card), " to the supply")
                        game.removePlayedCard(card)
                        game.playerLostCard(player, card)
                        game.addToSupply(card.name)
                    }
                }

                val gainCardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                    deck = Deck.FairyTale
                    cardName = card.name
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards to gain and then click Done."
                }

                game.supplyMap.values
                        .filterTo (gainCardAction.cards) { game.getCardCost(it) <= 3 && !it.costIncludesPotion && game.isCardInSupply(it) }

                if (gainCardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, gainCardAction)
                }
            }
            "Minion" -> when (choice) {
                "coins" -> {
                    player.addCoins(2)
                    game.addHistory(player.username, " chose +2 Coins")
                }
                "discard" -> {
                    game.addHistory(player.username, " chose to discard all players hands and have them draw 4 cards")
                    for (c in player.hand) {
                        game.playerDiscardedCard(player, c)
                    }
                    player.discardHand()
                    player.drawCards(4)
                    players
                            .filter { it.userId != player.userId }
                            .forEach {
                                if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(it.userId)) {
                                    game.addHistory(it.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                                } else if (!it.hasMoat() && !it.hasLighthouse() && it.hand.size >= 5) {
                                    for (c in it.hand) {
                                        game.playerDiscardedCard(it, c)
                                    }
                                    it.discardHand()
                                    it.drawCards(4)
                                } else {
                                    when {
                                        it.hasLighthouse() -> game.addHistory(it.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                                        it.hasMoat() -> game.addHistory(it.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                                        else -> game.addHistory(it.username, " had less than 5 cards")
                                    }
                                }
                            }
                    game.refreshAllPlayersHand()
                    game.refreshAllPlayersDiscard()
                    game.refreshAllPlayersCardsBought()
                }
            }
            "Mountebank" -> when (choice) {
                "discard" -> {
                    player.discardCardFromHand(Curse.NAME)
                    game.addHistory(player.username, " discarded a ", KingdomUtil.getWordWithBackgroundColor("Curse", Card.CURSE_COLOR), " card")
                    game.refreshHandArea(player)
                }
                "gain" -> {
                    if (game.isCardInSupply(Curse.NAME)) {
                        game.playerGainedCard(player, game.curseCard)
                    }
                    if (game.isCardInSupply(Copper.NAME)) {
                        game.playerGainedCard(player, game.copperCard)
                    }
                    game.refreshDiscard(player)
                }
            }
            "Native Village" -> if (choice == "card") {
                val topDeckCard = player.removeTopDeckCard()
                when {
                    topDeckCard != null -> {
                        player.nativeVillageCards.add(topDeckCard)
                        game.addHistory(player.username, " added ", player.pronoun, " top deck card to ", player.pronoun, " ", KingdomUtil.getWordWithBackgroundColor("Native Village", Card.ACTION_COLOR))
                    }
                    else -> game.addHistory(player.username, " did not have any cards to draw")
                }
            } else if (choice == "hand") {
                for (card in player.nativeVillageCards) {
                    player.addCardToHand(card)
                }
                player.nativeVillageCards.clear()
                game.addHistory(player.username, " added ", KingdomUtil.getWordWithBackgroundColor("Native Village", Card.ACTION_COLOR), " cards to ", player.pronoun, " hand")
            }
            "Navigator" -> when (choice) {
                "discard" -> {
                    player.discard.addAll(oldCardAction.cards)
                    for (card in oldCardAction.cards) {
                        game.playerDiscardedCard(player, card)
                    }
                    game.addHistory(player.username, " chose to discard the cards")
                }
                "deck" -> {
                    val sortCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER).apply {
                        deck = Deck.Seaside
                        isHideOnSelect = true
                        numCards = oldCardAction.cards.size
                        cardName = "Navigator"
                        cards = oldCardAction.cards
                        buttonValue = "Done"
                        instructions = "Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)"
                    }
                    game.setPlayerCardAction(player, sortCardAction)
                    game.addHistory(player.username, " chose to put the cards back on ", player.pronoun, " deck")
                }
            }
            "Noble Brigand" -> {
                val nextPlayer = game.playerMap[oldCardAction.playerId]!!
                val cardToTrash: Card
                val cardToDiscard: Card
                when (choice) {
                    "silver" -> {
                        cardToTrash = game.silverCard
                        cardToDiscard = game.goldCard
                    }
                    else -> {
                        cardToTrash = game.goldCard
                        cardToDiscard = game.silverCard
                    }
                }
                game.addHistory(player.username, " trashed ", nextPlayer.username, "'s ", KingdomUtil.getCardWithBackgroundColor(cardToTrash))
                game.playerGainedCard(player, cardToTrash)

                player.addCardToDiscard(cardToDiscard)
                game.playerDiscardedCard(nextPlayer, cardToDiscard)
                game.refreshDiscard(nextPlayer)
            }
            "Nobles" -> when (choice) {
                "cards" -> {
                    player.drawCards(3)
                    game.addHistory(player.username, " chose +3 Cards")
                }
                "actions" -> {
                    player.addActions(2)
                    game.addHistory(player.username, " chose +2 Actions")
                    game.refreshAllPlayersCardsPlayed()
                }
            }
            "Oracle" -> {
                val affectedPlayer = game.playerMap[oldCardAction.playerId]!!
                when (choice) {
                    "discard" -> {
                        game.incompleteCard!!.setPlayerActionCompleted(affectedPlayer.userId)
                        if (game.isCurrentPlayer(affectedPlayer)) {
                            game.addHistory(player.username, " chose to discard the top cards of ", affectedPlayer.pronoun, " deck")
                        } else {
                            game.addHistory(player.username, " chose to discard the top cards of ", affectedPlayer.username, "'s deck")
                        }
                        for (card in oldCardAction.cards) {
                            affectedPlayer.addCardToDiscard(card)
                            game.playerDiscardedCard(affectedPlayer, card)
                        }
                        game.refreshDiscard(affectedPlayer)
                    }
                    "back" -> {
                        if (game.isCurrentPlayer(affectedPlayer)) {
                            game.addHistory(player.username, " chose to put back the top cards of ", affectedPlayer.pronoun, " deck")
                        } else {
                            game.addHistory(player.username, " chose to put back the top cards of ", affectedPlayer.username, "'s deck")
                        }
                        if (oldCardAction.cards.size == 1 || KingdomUtil.uniqueCardList(oldCardAction.cards).size == 1) {
                            game.incompleteCard!!.setPlayerActionCompleted(affectedPlayer.userId)
                            for (card in oldCardAction.cards) {
                                affectedPlayer.addCardToTopOfDeck(card)
                            }
                        } else {
                            val reorderCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER).apply {
                                deck = Deck.Hinterlands
                                isHideOnSelect = true
                                numCards = 2
                                cardName = oldCardAction.cardName
                                cards.addAll(oldCardAction.cards)
                                buttonValue = "Done"
                                instructions = "Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)"
                            }
                            game.setPlayerCardAction(affectedPlayer, reorderCardAction)
                        }
                    }
                }
                if (!player.isShowCardAction && (!game.hasIncompleteCard() || game.incompleteCard!!.extraOldCardActions.isEmpty())) {
                    player.drawCards(2)
                }
            }
            "Pawn" -> {
                when (choice) {
                    "cardAndAction" -> {
                        player.drawCards(1)
                        player.addActions(1)
                        game.addHistory(player.username, " chose +1 Card, +1 Action")
                    }
                    "cardAndBuy" -> {
                        player.drawCards(1)
                        player.addBuys(1)
                        game.addHistory(player.username, " chose +1 Card, +1 Buy")
                    }
                    "cardAndCoin" -> {
                        player.drawCards(1)
                        player.addCoins(1)
                        game.addHistory(player.username, " chose +1 Card, +1 Coin")
                    }
                    "actionAndBuy" -> {
                        player.addActions(1)
                        player.addBuys(1)
                        game.addHistory(player.username, " chose +1 Action, +1 Buy")
                    }
                    "actionAndCoin" -> {
                        player.addActions(1)
                        player.addCoins(1)
                        game.addHistory(player.username, " chose +1 Action, +1 Coin")
                    }
                    "buyAndCoin" -> {
                        player.addBuys(1)
                        player.addCoins(1)
                        game.addHistory(player.username, " chose +1 Buy, +1 Coin")
                    }
                }
                game.refreshAllPlayersPlayingArea()
            }
            "Pirate Ship" -> when (choice) {
                "attack" -> {
                    incompleteCard = SinglePlayerIncompleteCard(oldCardAction.cardName, game)
                    var nextPlayerIndex = game.nextPlayerIndex
                    while (nextPlayerIndex != game.currentPlayerIndex) {
                        val nextPlayer = players[nextPlayerIndex]
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                            game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                        } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                            val nextCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                            nextCardAction.deck = Deck.Seaside
                            nextCardAction.playerId = nextPlayer.userId
                            nextCardAction.cardName = "Pirate Ship"
                            val card1 = nextPlayer.removeTopDeckCard()
                            if (card1 != null) {
                                if (!card1.isTreasure) {
                                    card1.isDisableSelect = true
                                }
                                nextCardAction.cards.add(card1)
                                val card2 = nextPlayer.removeTopDeckCard()
                                if (card2 != null) {
                                    if (!card2.isTreasure) {
                                        card2.isDisableSelect = true
                                    }
                                    nextCardAction.cards.add(card2)
                                    var instructions = "These are the top two cards from " + nextPlayer.username + "'s deck."
                                    if (card1.isTreasure || card2.isTreasure) {
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
                                    game.addHistory("The top card from ", nextPlayer.username, "'s deck was ", KingdomUtil.getArticleWithCardName(card1))
                                    if (card1.isTreasure) {
                                        game.addHistory(player.username, " trashed ", nextPlayer.username, "'s ", KingdomUtil.getWordWithBackgroundColor(card1.name, Card.TREASURE_COLOR))
                                        game.trashedCards.add(card1)
                                        game.playerLostCard(player, card1)
                                    }
                                }
                            } else {
                                game.addHistory(nextPlayer.username, " did not have any cards to draw")
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
                        val pirateAttackAction = incompleteCard.extraOldCardActions.remove()
                        game.setPlayerCardAction(player, pirateAttackAction)
                    }
                }
                "coins" -> {
                    player.addCoins(player.pirateShipCoins)
                    game.addHistory(player.username, " used ", KingdomUtil.getPlural(player.pirateShipCoins, "Pirate Ship Coin"))
                }
            }
            "Rancher" -> when (choice) {
                "cattle" -> {
                    player.addCattleTokens(1)
                    game.addHistory(player.username, " chose +1 cattle token")
                }
                "buy" -> {
                    player.addBuys(1)
                    game.addHistory(player.username, " chose +1 Buy")
                    game.refreshAllPlayersCardsBought()
                }
            }
            "Sorceress" -> if (choice != "none") {
                var showTrashCardAction = false
                when (choice) {
                    "cards" -> {
                        player.drawCards(2)
                        game.addHistory(player.username, " chose to get +2 Cards")
                    }
                    "actions" -> {
                        player.addActions(2)
                        game.refreshAllPlayersCardsPlayed()
                        game.addHistory(player.username, " chose to get +2 Actions")
                    }
                    "coins" -> {
                        player.addCoins(2)
                        game.refreshAllPlayersCardsPlayed()
                        game.addHistory(player.username, " chose to get +2 Coins")
                    }
                    "buys" -> {
                        player.addBuys(2)
                        game.refreshAllPlayersCardsBought()
                        game.addHistory(player.username, " chose to get +2 Buys")
                    }
                    "trash" -> {
                        game.addHistory(player.username, " chose to trash 2 Cards")
                        if (player.hand.size == 1) {
                            game.trashedCards.add(player.hand[0])
                            game.playerLostCard(player, player.hand[0])
                            player.removeCardFromHand(player.hand[0])
                            game.addHistory(player.username, " trashed the last card in ", player.pronoun, " hand")
                        } else if (player.hand.size >= 2) {
                            showTrashCardAction = true
                            val trashCardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND).apply {
                                deck = Deck.FairyTale
                                cardName = oldCardAction.cardName
                                buttonValue = "Done"
                                numCards = 2
                                instructions = "Select two cards to trash."
                                cards = player.hand
                                phase = oldCardAction.phase
                            }
                            oldCardAction.choices
                                    .filterTo (trashCardAction.choices) { it.value != choice }
                            if (oldCardAction.phase == 1) {
                                trashCardAction.choices.add(CardActionChoice("None", "none"))
                            }
                            game.setPlayerCardAction(player, trashCardAction)
                        }
                    }
                }

                val cursesRemaining = game.supply[Curse.NAME]!!
                if (cursesRemaining > 0 && oldCardAction.phase > 1) {
                    game.playerGainedCard(player, game.curseCard)
                }

                if (!showTrashCardAction && (cursesRemaining > 0 || oldCardAction.phase == 1)) {

                    val nextCardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                    nextCardAction.deck = Deck.FairyTale
                    nextCardAction.cardName = oldCardAction.cardName

                    if (cursesRemaining == 0) {
                        nextCardAction.instructions = "There are no curses remaining so you may only choose one more effect."
                    } else {
                        nextCardAction.instructions = "Choose another effect to apply (you will gain a curse), or click None if you don't want to apply any more effects."
                    }

                    oldCardAction.choices
                            .filterTo (nextCardAction.choices) { it.value != choice }

                    if (oldCardAction.phase == 1) {
                        nextCardAction.choices.add(CardActionChoice("None", "none"))
                    }

                    if (oldCardAction.choices.size > 1) {
                        nextCardAction.phase = oldCardAction.phase + 1
                        game.setPlayerCardAction(player, nextCardAction)
                    }
                }
            }
            "Spice Merchant" -> when (choice) {
                "cards" -> {
                    player.drawCards(2)
                    player.addActions(1)
                    game.refreshAllPlayersCardsPlayed()
                    game.addHistory(player.username, " chose +2 Cards and +1 Action")
                }
                "money" -> {
                    player.addCoins(2)
                    player.addBuys(1)
                    game.refreshAllPlayersCardsBought()
                    game.addHistory(player.username, " chose +$2 and +1 Buy")
                }
            }
            "Steward" -> when (choice) {
                "cards" -> {
                    player.drawCards(2)
                    game.addHistory(player.username, " chose +2 Cards")
                }
                "coins" -> {
                    player.addCoins(2)
                    game.addHistory(player.username, " chose +2 Coins")
                }
                "trash" -> if (player.hand.size > 0) {
                    if (player.hand.size <= 2) {
                        val cards = ArrayList(player.hand)
                        for (card in cards) {
                            player.removeCardFromHand(card)
                            game.addHistory(player.username, " trashed ", KingdomUtil.getArticleWithCardName(card))
                        }
                    } else {
                        val trashCardsCardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND).apply {
                            deck = Deck.Intrigue
                            cardName = "Steward"
                            buttonValue = "Done"
                            numCards = 2
                            instructions = "Select two cards to trash."
                            cards = player.hand
                        }
                        game.setPlayerCardAction(player, trashCardsCardAction)
                    }
                }
            }
            "Trusty Steed" -> {
                when (choice) {
                    "cardsAndActions" -> {
                        player.drawCards(2)
                        player.addActions(2)
                        game.addHistory(player.username, " chose +2 Cards, +2 Actions")
                    }
                    "cardsAndCoins" -> {
                        player.drawCards(2)
                        player.addCoins(2)
                        game.addHistory(player.username, " chose +2 Cards, +2 Coins")
                    }
                    "cardsAndSilvers" -> {
                        player.drawCards(2)
                        game.playerGainedCard(player, game.silverCard)
                        game.playerGainedCard(player, game.silverCard)
                        game.playerGainedCard(player, game.silverCard)
                        game.playerGainedCard(player, game.silverCard)
                        player.discard.addAll(player.deck)
                        player.deck.clear()
                        game.addHistory(player.username, " chose +2 Cards, gain 4 Silvers and put your deck into your discard pile")
                    }
                    "actionsAndCoins" -> {
                        player.addActions(2)
                        player.addCoins(2)
                        game.addHistory(player.username, " chose +2 Actions, +2 Coins")
                    }
                    "actionsAndSilvers" -> {
                        player.addActions(2)
                        game.playerGainedCard(player, game.silverCard)
                        game.playerGainedCard(player, game.silverCard)
                        game.playerGainedCard(player, game.silverCard)
                        game.playerGainedCard(player, game.silverCard)
                        player.discard.addAll(player.deck)
                        player.deck.clear()
                        game.addHistory(player.username, " chose +2 Actions, gain 4 Silvers and put your deck into your discard pile")
                    }
                    "coinsAndSilvers" -> {
                        player.addCoins(2)
                        game.playerGainedCard(player, game.silverCard)
                        game.playerGainedCard(player, game.silverCard)
                        game.playerGainedCard(player, game.silverCard)
                        game.playerGainedCard(player, game.silverCard)
                        player.discard.addAll(player.deck)
                        player.deck.clear()
                        game.addHistory(player.username, " chose +2 Coins, gain 4 Silvers and put your deck into your discard pile")
                    }
                }
                game.refreshAllPlayersPlayingArea()
            }
            "Torturer" -> when (choice) {
                "discard" -> {
                    var cardsToDiscard = 2
                    if (player.hand.size == 1) {
                        cardsToDiscard = 1
                    }
                    if (player.hand.size > 0) {
                        val discardCardsAction = OldCardAction(OldCardAction.TYPE_DISCARD_FROM_HAND).apply {
                            deck = Deck.Intrigue
                            cardName = "Torturer"
                            buttonValue = "Done"
                            numCards = cardsToDiscard
                            instructions = "Select two cards to discard."
                            cards = player.hand
                        }
                        game.setPlayerCardAction(player, discardCardsAction)
                    }
                }
                "curse" -> {
                    game.addHistory(player.username, " chose to gain a ", KingdomUtil.getWordWithBackgroundColor("Curse", Card.CURSE_COLOR))
                    if (game.isCardInSupply(Curse.NAME)) {
                        game.playerGainedCardToHand(player, game.curseCard)
                        player.addCardToHand(game.curseCard)
                        game.refreshHand(player)
                    }
                }
            }
            "Tournament" -> when (choice) {
                "prize" -> when {
                    game.prizeCards.isEmpty() -> game.addHistory(player.username, " chose to gain a Prize but there were no more available")
                    game.prizeCards.size == 1 -> {
                        game.playerGainedCardToTopOfDeck(player, game.prizeCards[0], false)
                        game.prizeCards.clear()
                    }
                    else -> {
                        val choosePrizeCardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS).apply {
                            deck = Deck.Cornucopia
                            cardName = oldCardAction.cardName
                            numCards = 1
                            buttonValue = "Done"
                            instructions = "Select one of the following cards to gain and then click Done."
                            cards.addAll(game.prizeCards)
                        }
                        game.setPlayerCardAction(player, choosePrizeCardAction)
                    }
                }
                "duchy" -> when {
                    game.isCardInSupply(Duchy.NAME) -> {
                        game.playerGainedCardToTopOfDeck(player, game.duchyCard)
                        game.addHistory(player.username, " chose to gain a ", KingdomUtil.getCardWithBackgroundColor(game.duchyCard))
                    }
                    else -> game.addHistory(player.username, " chose to gain a ", KingdomUtil.getCardWithBackgroundColor(game.duchyCard), " but there were no more in the supply")
                }
            }
            "Trader" -> {
                val cardToGain = oldCardAction.associatedCard!!
                when (choice) {
                    "silver" -> {
                        game.addHistory(player.username, " revealed ", KingdomUtil.getWordWithBackgroundColor("Trader", Card.ACTION_REACTION_COLOR), " to gain ", KingdomUtil.getArticleWithCardName(game.silverCard), " instead")
                        if (game.supply[Silver.NAME]!! > 0) {
                            game.playerGainedCard(player, game.silverCard)
                        }
                    }
                    else -> when (oldCardAction.destination) {
                        CardLocation.Hand -> game.playerGainedCardToHand(player, cardToGain)
                        CardLocation.Deck -> game.playerGainedCardToTopOfDeck(player, cardToGain)
                        CardLocation.Discard -> game.playerGainedCard(player, cardToGain)
                    }
                }
            }
            "Watchtower" -> {
                val cardToGain = oldCardAction.cards[0]
                when (choice) {
                    "trash" -> {
                        game.addHistory(player.username, " revealed ", KingdomUtil.getWordWithBackgroundColor("Watchtower", Card.ACTION_REACTION_COLOR), " to trash ", KingdomUtil.getArticleWithCardName(cardToGain))
                        game.moveGainedCard(player, cardToGain, CardLocation.Trash)
                    }
                    "deck" -> {
                        game.addHistory(player.username, " revealed ", KingdomUtil.getWordWithBackgroundColor("Watchtower", Card.ACTION_REACTION_COLOR), " to put ", KingdomUtil.getArticleWithCardName(cardToGain), " on top of ", player.pronoun, " deck")
                        game.moveGainedCard(player, cardToGain, CardLocation.Deck)
                    }
                    else -> when (oldCardAction.destination) {
                        CardLocation.Hand -> game.playerGainedCardToHand(player, cardToGain)
                        CardLocation.Deck -> game.playerGainedCardToTopOfDeck(player, cardToGain)
                        CardLocation.Discard -> game.playerGainedCard(player, cardToGain)
                    }
                }
            }
            "Young Witch" -> when (choice) {
                "reveal" -> game.addHistory(player.username, " revealed a Bane card")
                "curse" -> if (game.supply[Curse.NAME]!! > 0) {
                    game.playerGainedCard(player, game.curseCard)
                }
            }
        }
        return incompleteCard
    }
}
