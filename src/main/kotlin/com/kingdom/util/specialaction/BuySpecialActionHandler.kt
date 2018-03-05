package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object BuySpecialActionHandler {

    fun getCardAction(game: Game, player: Player, card: Card): OldCardAction? {

        when (card.name) {
            "Botanical Gardens" -> when {
                player.coins >= 6 -> {
                    val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                    cardAction.deck = Deck.Proletariat
                    cardAction.cardName = card.name
                    cardAction.associatedCard = card
                    cardAction.instructions = "Do you want to pay an additional 3 coins to gain another Botanical Gardens or an additional 6 coins to gain two more Botanical Gardens?"
                    cardAction.choices.add(CardActionChoice("3 more coins", "3"))
                    cardAction.choices.add(CardActionChoice("6 more coins", "6"))
                    cardAction.choices.add(CardActionChoice("No", "no"))
                    return cardAction
                }
                player.coins >= 3 -> {
                    val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Proletariat
                    cardAction.cardName = card.name
                    cardAction.associatedCard = card
                    cardAction.instructions = "Do you want to pay an additional 3 coins to gain another Botanical Gardens?"
                    return cardAction
                }
            }
            "City Planner" -> when {
                player.coins >= 2 && !player.getVictoryCards().isEmpty() -> {
                    val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Proletariat
                    cardAction.cardName = card.name
                    cardAction.associatedCard = card
                    cardAction.instructions = "Do you want to pay an additional 2 coins to set aside a victory card from your hand?"
                    return cardAction
                }
            }
            "Farmland" -> when {
                !player.hand.isEmpty() -> {
                    val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                    cardAction.deck = Deck.Hinterlands
                    cardAction.cardName = card.name
                    cardAction.buttonValue = "Done"
                    cardAction.numCards = 1
                    cardAction.associatedCard = card
                    cardAction.instructions = "Select a card to trash."
                    cardAction.cards = KingdomUtil.uniqueCardList(player.hand)
                    return cardAction
                }
                else -> game.addHistory(player.username, " did not have any cards in ", player.pronoun, " hand")
            }
            "Orchard" -> when {
                player.coins > 1 -> {
                    val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Proletariat
                    cardAction.cardName = card.name
                    cardAction.associatedCard = card
                    cardAction.instructions = "Do you want to pay an additional 2 coins to gain two fruit tokens?"
                    return cardAction
                }
            }
            "Rancher" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
                cardAction.deck = Deck.Proletariat
                cardAction.cardName = cardAction.cardName
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.associatedCard = card
                cardAction.instructions = "Select one of the following cards to gain and then click Done."
                val maxCost = player.hand.size * 2
                for (c in game.supplyMap.values) {
                    if (c.isAction && game.getCardCost(c) <= maxCost && !c.costIncludesPotion && game.isCardInSupply(c)) {
                        cardAction.cards.add(c)
                    }
                }
                if (cardAction.cards.size > 0) {
                    return cardAction
                }
            }
            "Squatter" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                cardAction.deck = Deck.Proletariat
                cardAction.cardName = card.name
                cardAction.associatedCard = card
                cardAction.instructions = "Do you want to return this card to the supply and have each other player gain a Squatter?"
                return cardAction
            }
            "Shepherd" -> if (player.coins >= 2) {
                val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                cardAction.deck = Deck.Proletariat
                cardAction.cardName = card.name
                cardAction.associatedCard = card
                cardAction.instructions = "Do you want to pay an additional 2 coins to gain 2 cattle tokens?"
                return cardAction
            }
        }

        return null
    }

    fun getHagglerCardAction(game: Game, card: Card): OldCardAction? {
        val cardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
        cardAction.deck = Deck.Hinterlands
        cardAction.cardName = "Haggler"
        cardAction.buttonValue = "Done"
        cardAction.numCards = 1
        cardAction.associatedCard = card
        cardAction.instructions = "Select one of the following cards to gain and then click Done."
        val cost = game.getCardCost(card)
        game.supplyMap.values
                .filterTo (cardAction.cards) { !it.isVictory && game.getCardCost(it) < cost && (!it.costIncludesPotion || card.costIncludesPotion) && game.isCardInSupply(it) }
        return if (cardAction.cards.size > 0) {
            cardAction
        } else null
    }

    fun setNobleBrigandCardAction(game: Game, player: Player) {
        var playerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
        while (playerIndex != game.currentPlayerIndex) {
            val nextPlayer = game.players[playerIndex]
            if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
            } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                val cards = ArrayList<Card>()
                val card1 = nextPlayer.removeTopDeckCard()
                var card2: Card? = null
                if (card1 != null) {
                    cards.add(card1)
                    card2 = nextPlayer.removeTopDeckCard()
                    if (card2 != null) {
                        cards.add(card2)
                    }
                }
                var revealedTreasure = false
                if (!cards.isEmpty()) {
                    var numApplicableCards = 0
                    for (c in cards) {
                        if (c.isTreasure) {
                            revealedTreasure = true
                            if (c.isSilver || c.isGold) {
                                numApplicableCards++
                            }
                        }
                    }
                    game.addHistory(nextPlayer.username, " revealed ", KingdomUtil.groupCards(cards, true))
                    when {
                        numApplicableCards == 1 || numApplicableCards == 2 && card1!!.cardId == card2!!.cardId -> {
                            val applicableCard: Card?
                            if (card1!!.isSilver || card1.isGold) {
                                applicableCard = card1
                            } else {
                                applicableCard = card2
                            }
                            game.trashedCards.add(applicableCard!!)
                            if (numApplicableCards == 2) {
                                player.addCardToDiscard(applicableCard)
                                game.playerDiscardedCard(nextPlayer, applicableCard)
                                game.refreshDiscard(nextPlayer)
                            }
                            game.addHistory(player.username, " trashed ", nextPlayer.username, "'s ", KingdomUtil.getCardWithBackgroundColor(applicableCard))
                            game.playerGainedCard(player, applicableCard)
                        }
                        numApplicableCards == 2 -> {
                            val nextCardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                            nextCardAction.deck = Deck.Hinterlands
                            nextCardAction.playerId = nextPlayer.userId
                            nextCardAction.cardName = "Noble Brigand"
                            nextCardAction.choices.add(CardActionChoice("Gold", "gold"))
                            nextCardAction.choices.add(CardActionChoice("Silver", "silver"))
                            nextCardAction.instructions = "Do you want to trash " + nextPlayer.username + "'s Gold or Silver?"
                            nextCardAction.cards.addAll(cards)
                            game.setPlayerCardAction(player, nextCardAction)
                        }
                        else -> {
                            for (c in cards) {
                                nextPlayer.addCardToDiscard(c)
                                game.playerDiscardedCard(nextPlayer, c)
                            }
                            game.refreshDiscard(nextPlayer)
                        }
                    }
                }
                if (!revealedTreasure) {
                    game.playerGainedCard(nextPlayer, game.copperCard)
                }
            } else {
                if (nextPlayer.hasLighthouse()) {
                    game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                } else {
                    game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                }
            }
            playerIndex = game.calculateNextPlayerIndex(playerIndex)
        }
    }
}
