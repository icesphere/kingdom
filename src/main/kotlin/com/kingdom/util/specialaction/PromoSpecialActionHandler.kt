package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object PromoSpecialActionHandler {
    fun handleSpecialAction(game: Game, card: Card): IncompleteCard? {

        val players = game.players
        var incompleteCard: IncompleteCard? = null

        when (card.name) {
            "Black Market" -> {
                val player = game.currentPlayer
                val cardAction: CardAction
                val cards = ArrayList<Card>()
                var blackMarketCard = game.removeNextBlackMarketCard()
                while (blackMarketCard != null && cards.size < 3) {
                    cards.add(blackMarketCard)
                    blackMarketCard = game.removeNextBlackMarketCard()
                }
                var canAffordAny = false
                if (cards.size > 0) {
                    game.addHistory("Black Market cards: " + KingdomUtil.getCardNames(cards))
                    game.blackMarketCardsToBuy = cards
                    var coins = player!!.coins
                    var potions = player.potions
                    if (game.isPlayTreasureCards) {
                        coins += player.coinsInHand
                        potions += player.potionsInHand
                    }
                    for (c in cards) {
                        if (coins >= game.getCardCostBuyPhase(c) && (!c.costIncludesPotion || potions > 0)) {
                            canAffordAny = true
                            break
                        }
                    }
                    if (canAffordAny) {
                        cardAction = CardAction(CardAction.TYPE_YES_NO)
                        cardAction.deck = Deck.Promo
                        val instructions = StringBuffer("You have ")
                        instructions.append(KingdomUtil.getPlural(coins, "coin"))
                        if (game.isUsePotions) {
                            instructions.append(" and ").append(KingdomUtil.getPlural(potions, "potion"))
                        }
                        instructions.append(". Do you want to buy one of these cards?")
                        cardAction.instructions = instructions.toString()
                    } else {
                        cardAction = CardAction(CardAction.TYPE_CHOOSE_IN_ORDER)
                        cardAction.deck = Deck.Promo
                        cardAction.isHideOnSelect = true
                        cardAction.numCards = cards.size
                        cardAction.buttonValue = "Done"
                        cardAction.instructions = "You don't have enough coins to buy any of these black market cards. Click the cards in the order you want them to be on the bottom of the black market deck, starting with the top card and then click Done. (The last card you click will be the bottom card of the black market deck)"
                    }
                    cardAction.cards = cards
                } else {
                    cardAction = CardAction(CardAction.TYPE_CHOOSE_CARDS)
                    cardAction.deck = Deck.Promo
                    cardAction.numCards = 0
                    cardAction.buttonValue = "Continue"
                    cardAction.instructions = "There are no more black market cards to buy. Click Continue."
                }
                cardAction.cardName = card.name
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Envoy" -> {
                val player = game.currentPlayer
                val playerToLeft = players[game.nextPlayerIndex]
                val cards = ArrayList<Card>()
                var hasMoreCards = true
                while (hasMoreCards && cards.size < 5) {
                    val c = player!!.removeTopDeckCard()
                    if (c == null) {
                        hasMoreCards = false
                    } else {
                        cards.add(c)
                    }
                }
                if (cards.size > 0) {
                    incompleteCard = MultiPlayerIncompleteCard(card.name, game, playerToLeft.userId)
                    val cardAction = CardAction(CardAction.TYPE_CHOOSE_CARDS)
                    cardAction.deck = Deck.Promo
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.cards = cards
                    cardAction.instructions = "Select the card you want " + player!!.username + " to discard and then click Done."
                    game.setPlayerCardAction(playerToLeft, cardAction)
                    incompleteCard.allActionsSet()
                }
            }
            "Governor" -> {
                val player = game.currentPlayer
                val cardAction = CardAction(CardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Promo
                cardAction.cardName = card.name
                cardAction.instructions = "Choose one: you get the version in parentheses. Each player gets +1 (+3) cards; or each player gains a Silver (Gold); or each player may trash a card from his hand and gain a card costing exactly 1 (2) more."
                cardAction.choices.add(CardActionChoice("Cards", "cards"))
                cardAction.choices.add(CardActionChoice("Silver and Gold", "money"))
                cardAction.choices.add(CardActionChoice("Trash Card", "trash"))
                game.setPlayerCardAction(player!!, cardAction)
            }
        }

        return incompleteCard
    }
}