package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardColor
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil

import java.util.ArrayList

object BuySpecialActionHandler {

    fun getCardAction(game: OldGame, player: OldPlayer, card: Card): OldCardAction? {

        when (card.name) {
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
        }

        return null
    }

    fun getHagglerCardAction(game: OldGame, card: Card): OldCardAction? {
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

    fun setNobleBrigandCardAction(game: OldGame, player: OldPlayer) {
        var playerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
        while (playerIndex != game.currentPlayerIndex) {
            val nextPlayer = game.players[playerIndex]
            if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
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
                        numApplicableCards == 1 || numApplicableCards == 2 && card1!!.name == card2!!.name -> {
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
                    game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                } else {
                    game.addHistory(nextPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                }
            }
            playerIndex = game.calculateNextPlayerIndex(playerIndex)
        }
    }
}
