package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil
import java.util.*

object GainCardsHandler {
    fun handleCardAction(game: OldGame, player: OldPlayer, oldCardAction: OldCardAction, selectedCardNames: List<String>) {

        val type = oldCardAction.type
        val cardMap = game.cardMap

        if (oldCardAction.cardName != "Tournament" && oldCardAction.cardName != "Museum" && oldCardAction.cardName != "Artisan") {
            selectedCardNames
                    .map { cardMap[it]!! }
                    .forEach {
                        when (type) {
                            OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY,
                            OldCardAction.TYPE_GAIN_UP_TO_FROM_SUPPLY -> game.playerGainedCard(player, it)
                            else -> game.playerGainedCard(player, it, false)
                        }
                    }
        }

        when (oldCardAction.cardName) {
            "Artisan" -> {
                val card = cardMap[selectedCardNames[0]]!!

                game.playerGainedCardToHand(player, card)

                game.refreshPlayingArea(player)

                val putCardOnTopOfDeckAction = OldCardAction(OldCardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK).apply {
                    deck = Deck.Kingdom
                    cardName = "Artisan"
                    cards.addAll(player.hand)
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select a card from your hand to put on top of your deck."
                }
                game.setPlayerCardAction(player, putCardOnTopOfDeckAction)
            }
            "Black Market" -> {
                val cardBought = cardMap[selectedCardNames[0]]!!
                game.boughtBlackMarketCard(cardBought)
                game.blackMarketCardsToBuy.remove(cardBought)
                val chooseOrderCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER).apply {
                    deck = Deck.Promo
                    isHideOnSelect = true
                    numCards = game.blackMarketCardsToBuy.size
                    cardName = "Black Market"
                    cards = game.blackMarketCardsToBuy
                    buttonValue = "Done"
                    instructions = "Click the cards in the order you want them to be on the bottom of the black market deck, starting with the top card and then click Done. (The last card you click will be the bottom card of the black market deck)"
                }
                game.setPlayerCardAction(player, chooseOrderCardAction)
            }
            "Develop" -> if (oldCardAction.phase < 3) {
                val gainCardAction = OldCardAction(OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY).apply {
                    deck = Deck.Hinterlands
                    cardName = oldCardAction.cardName
                    buttonValue = "Done"
                    numCards = 1
                    instructions = "Select one of the following cards to gain and then click Done."
                    associatedCard = oldCardAction.associatedCard
                    phase = 3
                }

                val cards = ArrayList<Card>()
                var cost = game.getCardCost(gainCardAction.associatedCard!!)

                when (oldCardAction.phase) {
                    1 -> cost -= 1
                    2 -> cost += 1
                }

                game.supplyMap.values.filterTo(cards) {
                    game.getCardCost(it) == cost &&
                            oldCardAction.associatedCard!!.costIncludesPotion == it.costIncludesPotion &&
                            game.supply[it.name]!! > 0
                }

                gainCardAction.cards = cards
                game.setPlayerCardAction(player, gainCardAction)
            }
            "Horn of Plenty" -> {
                val card = cardMap[selectedCardNames[0]]!!
                if (card.isVictory) {
                    (game.cardsPlayed as LinkedList<*>).removeLastOccurrence(oldCardAction.associatedCard)
                    game.treasureCardsPlayed.remove(oldCardAction.associatedCard)
                    game.trashedCards.add(oldCardAction.associatedCard!!)
                    game.playerLostCard(player, oldCardAction.associatedCard!!)
                    game.addHistory(player.username, "'s ", KingdomUtil.getCardWithBackgroundColor(oldCardAction.associatedCard!!), " was trashed")
                    game.refreshAllPlayersCardsPlayed()
                }
            }
            "Ironworks" -> {
                val card = cardMap[selectedCardNames[0]]!!
                when {
                    card.isAction -> {
                        player.addActions(1)
                        game.refreshAllPlayersCardsPlayed()
                    }
                    card.isTreasure -> player.addCoins(1)
                    card.isVictory -> player.drawCards(1)
                }
            }
            "Museum" -> {
                val card = cardMap[selectedCardNames[0]]!!
                game.playerGainedCard(player, card, false)
                game.prizeCards.remove(card)
            }
            "Tournament" -> {
                val card = cardMap[selectedCardNames[0]]!!
                game.playerGainedCardToTopOfDeck(player, card, false)
                game.prizeCards.remove(card)
            }
            "University" -> if (selectedCardNames.isEmpty()) {
                game.addHistory(player.username, " chose not to gain a card with ", KingdomUtil.getWordWithBackgroundColor("University", Card.ACTION_COLOR))
            }
        }
    }
}
