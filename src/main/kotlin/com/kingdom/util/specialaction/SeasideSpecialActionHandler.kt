package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardColor
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Province
import com.kingdom.model.cards.supply.Silver
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object SeasideSpecialActionHandler {
    fun handleSpecialAction(game: OldGame, card: Card, repeatedAction: Boolean): IncompleteCard? {

        val supplyMap = game.supplyMap
        val supply = game.supply
        val players = game.players
        val currentPlayerId = game.currentPlayerId
        var incompleteCard: IncompleteCard? = null

        when (card.name) {
            "Ambassador" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                    cardAction.deck = Deck.Seaside
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select the card that you want other players to receive and then click Done."
                    if (!game.blackMarketCards.isEmpty()) {
                        cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    } else {
                        for (c in KingdomUtil.uniqueCardList(player.hand)) {
                            if (supply[c.name] != null) {
                                cardAction.cards.add(c)
                            }
                        }
                    }
                    if (!cardAction.cards.isEmpty()) {
                        game.setPlayerCardAction(player, cardAction)
                    } else {
                        game.addHistory(player.username, " did not have any cards from the supply in ", player.pronoun, " hand")
                    }
                } else {
                    game.addHistory(player.username, " did not have any cards in ", player.pronoun, " hand")
                }
            }
            "Cutpurse" -> for (player in players) {
                if (player.userId != currentPlayerId) {
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                        game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                    } else if (!player.hasMoat() && !player.hasLighthouse()) {
                        var hasCopper = false
                        for (handCard in player.hand) {
                            if (handCard.name == "Copper") {
                                hasCopper = true
                                player.discardCardFromHand(handCard)
                                game.addHistory("The ", KingdomUtil.getWordWithBackgroundColor("Cutpurse", CardColor.Action), " discarded ", player.username, "'s ", KingdomUtil.getWordWithBackgroundColor("Copper", CardColor.Treasure))
                                game.refreshHand(player)
                                game.refreshDiscard(player)
                                break
                            }
                        }
                        if (!hasCopper) {
                            game.addHistory(player.username, " did not have a ", KingdomUtil.getWordWithBackgroundColor("Copper", CardColor.Treasure))
                        }
                    } else {
                        if (player.hasLighthouse()) {
                            game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                        } else {
                            game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                        }
                    }
                }
            }
            "Embargo" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Seaside
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select the card you want to place an embargo token on and then click Done."
                for (c in supplyMap.values) {
                    if (game.isCardInSupply(c)) {
                        cardAction.cards.add(c)
                    }
                }
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                }
                if (!repeatedAction) {
                    game.removePlayedCard(card)
                    game.trashedCards.add(card)
                    game.playerLostCard(player!!, card)
                }
            }
            "Explorer" -> {
                val player = game.currentPlayer
                var hasProvince = false
                for (c in player!!.hand) {
                    if (c.name == Province.NAME) {
                        hasProvince = true
                        break
                    }
                }
                if (hasProvince) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                    cardAction.deck = Deck.Seaside
                    cardAction.cardName = card.name
                    cardAction.instructions = "Do you want to reveal a Province and gain a Gold into your hand, or do you want to gain a silver into your hand?"
                    cardAction.choices.add(CardActionChoice("Gold", "gold"))
                    cardAction.choices.add(CardActionChoice("Silver", "silver"))
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    if (game.isCardInSupply(Silver.NAME)) {
                        game.playerGainedCardToHand(player, game.silverCard)
                        game.refreshHand(player)
                    }
                }
            }
            "Ghost Ship" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (player in players) {
                    if (player.userId != currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                            incompleteCard.setPlayerActionCompleted(player.userId)
                            game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                        } else if (!player.hasMoat() && player.hand.size >= 4 && !player.hasLighthouse()) {
                            val cardAction = OldCardAction(OldCardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK)
                            cardAction.deck = Deck.Seaside
                            cardAction.cardName = card.name
                            cardAction.cards.addAll(player.hand)
                            cardAction.numCards = player.hand.size - 3
                            cardAction.instructions = "Select down to 3 cards. Select the Cards you want to go on top of your deck and then click Done."
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
            "Haven" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Seaside
                cardAction.cardName = card.name
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.hand)
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select a card to set aside until your next turn."
                game.setPlayerCardAction(player, cardAction)
            }
            "Island" -> {
                val player = game.currentPlayer
                if (!repeatedAction) {
                    game.removePlayedCard(card)
                    player!!.islandCards.add(card)
                }
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Seaside
                cardAction.cardName = card.name
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.hand)
                cardAction.numCards = 1
                cardAction.instructions = "Select a card to be added to your island and then click Done."
                cardAction.buttonValue = "Done"
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.refreshHandArea(player)
                }
            }
            "Lookout" -> {
                val player = game.currentPlayer
                val cards = ArrayList<Card>()
                var hasMoreCards = true
                while (hasMoreCards && cards.size < 3) {
                    val c = player!!.removeTopDeckCard()
                    if (c == null) {
                        hasMoreCards = false
                    } else {
                        cards.add(c)
                    }
                }
                when {
                    cards.size == 1 -> {
                        game.trashedCards.add(cards[0])
                        game.playerLostCard(player!!, cards[0])
                        game.addHistory("The ", KingdomUtil.getWordWithBackgroundColor("Lookout", CardColor.Action), " trashed ", player.username, "'s ", cards[0].name)
                    }
                    cards.size > 0 -> {
                        game.refreshHand(player!!)
                        val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER)
                        cardAction.deck = Deck.Seaside
                        cardAction.isHideOnSelect = true
                        cardAction.numCards = cards.size
                        cardAction.cardName = card.name
                        cardAction.cards = cards
                        cardAction.buttonValue = "Done"
                        var instructions = "Click on the card you want to trash, then click on the card you want to discard, "
                        if (cards.size == 3) {
                            instructions += "then click on the card you want to put on top of your deck, "
                        }
                        instructions += "then click Done."
                        cardAction.instructions = instructions
                        game.setPlayerCardAction(player, cardAction)
                    }
                    else -> game.addHistory(player!!.username, " did not have any cards to draw")
                }
            }
            "Native Village" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Seaside
                cardAction.cardName = card.name
                cardAction.instructions = "Do you want to put your top deck card on your Native Village, or put all the cards from your Native Village into your hand?"
                cardAction.choices.add(CardActionChoice("Top Deck Card", "card"))
                cardAction.choices.add(CardActionChoice("Add Cards To Hand", "hand"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Navigator" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Seaside
                cardAction.cardName = card.name
                cardAction.instructions = "Do you want to discard these cards, or put them back on top of your deck?"
                cardAction.choices.add(CardActionChoice("Discard", "discard"))
                cardAction.choices.add(CardActionChoice("Put Back", "deck"))
                val topDeckCards = ArrayList<Card>()
                for (i in 0..4) {
                    val c = player!!.removeTopDeckCard() ?: break
                    topDeckCards.add(c)
                }
                if (topDeckCards.size > 0) {
                    cardAction.cards.addAll(topDeckCards)
                    game.setPlayerCardAction(player!!, cardAction)
                } else {
                    game.addHistory(player!!.username, " did not have any cards in " + player.pronoun, " deck")
                }
            }
            "Pearl Diver" -> {
                val player = game.currentPlayer
                val bottomCard = player!!.lookAtBottomDeckCard()
                if (bottomCard != null) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Seaside
                    cardAction.cards.add(bottomCard)
                    cardAction.cardName = card.name
                    cardAction.instructions = "This is the bottom card from your deck, do you want to put it on top of your deck?"
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.addHistory(player.username, " did not have any cards in " + player.pronoun, " deck")
                }
            }
            "Pirate Ship" -> {
                val player = game.currentPlayer
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Seaside
                cardAction.cardName = card.name
                cardAction.instructions = "Do you want to attack other players and try to get a Pirate Ship Coin, or do you want to get " + KingdomUtil.getPlural(player!!.pirateShipCoins, "Coin") + " from your Pirate Ship Coins?"
                cardAction.choices.add(CardActionChoice("Attack", "attack"))
                cardAction.choices.add(CardActionChoice("Use Coins", "coins"))
                game.setPlayerCardAction(player, cardAction)
            }
            "Salvager" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Seaside
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Sea Hag" -> for (player in players) {
                if (player.userId != currentPlayerId) {
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(player.userId)) {
                        game.addHistory(player.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                    } else if (!player.hasMoat() && !player.hasLighthouse()) {
                        val topCard = player.removeTopDeckCard()
                        if (topCard != null) {
                            player.addCardToDiscard(topCard)
                            game.playerDiscardedCard(player, topCard)
                            if (game.isCardInSupply(Curse.NAME)) {
                                game.playerGainedCardToTopOfDeck(player, game.curseCard)
                            }
                        }
                    } else {
                        if (player.hasLighthouse()) {
                            game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                        } else {
                            game.addHistory(player.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                        }
                    }
                }
            }
            "Smugglers" -> {
                val player = game.currentPlayer
                val oldCardAction: OldCardAction
                val cards = ArrayList<Card>()
                for (c in game.smugglersCards) {
                    if (game.isCardInSupply(c) && !c.costIncludesPotion) {
                        cards.add(c)
                    }
                }
                if (cards.size > 0) {
                    oldCardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
                    oldCardAction.deck = Deck.Seaside
                    oldCardAction.cardName = card.name
                    oldCardAction.buttonValue = "Done"
                    oldCardAction.cards = KingdomUtil.uniqueCardList(cards)
                    oldCardAction.numCards = 1
                    oldCardAction.instructions = "Select one of the following cards to gain and then click Done."
                } else {
                    oldCardAction = OldCardAction(OldCardAction.TYPE_INFO)
                    oldCardAction.deck = Deck.Seaside
                    oldCardAction.cardName = card.name
                    oldCardAction.instructions = "The player to your right did not gain any cards costing 6 or less that are still in the supply."
                    oldCardAction.buttonValue = "Close"
                }
                game.setPlayerCardAction(player!!, oldCardAction)
            }
            "Tactician" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    for (c in player.hand) {
                        game.playerDiscardedCard(player, c)
                    }
                    player.discardHand()
                    player.setTacticianBonus(true)
                }
            }
            "Treasure Map" -> {
                val player = game.currentPlayer
                var secondTreasureMap: Card? = null
                for (c in player!!.hand) {
                    if (c.name == "Treasure Map") {
                        secondTreasureMap = c
                        break
                    }
                }
                game.removePlayedCard(card)
                game.trashedCards.add(card)
                game.playerLostCard(player, card)
                if (secondTreasureMap != null) {
                    player.removeCardFromHand(secondTreasureMap)
                    game.trashedCards.add(secondTreasureMap)
                    game.playerLostCard(player, secondTreasureMap)
                    var goldCardsGained = 0
                    for (i in 0..3) {
                        if (game.isCardInSupply(Gold.NAME)) {
                            goldCardsGained++
                            game.playerGainedCardToTopOfDeck(player, game.goldCard)
                        } else {
                            break
                        }
                    }
                    game.addHistory(player.username, " trashed two ", KingdomUtil.getWordWithBackgroundColor("Treasure Maps", CardColor.Action), " and gained " + goldCardsGained + " " + KingdomUtil.getCardWithBackgroundColor(game.goldCard) + " cards on top of ", player.pronoun, " deck")
                } else {
                    game.addHistory(player.username, " trashed a ", KingdomUtil.getWordWithBackgroundColor("Treasure Map", CardColor.Action))
                }
            }
            "Warehouse" -> {
                val player = game.currentPlayer
                if (player!!.hand.size > 0) {
                    if (player.hand.size == 1) {
                        game.addHistory(player.username, " discarded 1 card")
                        val cardToDiscard = player.hand[0]
                        player.discardCardFromHand(cardToDiscard)
                        game.playerDiscardedCard(player, cardToDiscard)
                    } else {
                        var cardsToDiscard = 3
                        if (player.hand.size < 3) {
                            cardsToDiscard = player.hand.size
                        }
                        val cardAction = OldCardAction(OldCardAction.TYPE_DISCARD_FROM_HAND)
                        cardAction.deck = Deck.Seaside
                        cardAction.cardName = card.name
                        cardAction.cards.addAll(player.hand)
                        cardAction.numCards = cardsToDiscard
                        cardAction.instructions = "Select " + KingdomUtil.getPlural(cardsToDiscard, "card") + " to discard and then click Done."
                        cardAction.buttonValue = "Done"
                        game.setPlayerCardAction(player, cardAction)
                    }
                }
            }
        }

        return incompleteCard
    }
}