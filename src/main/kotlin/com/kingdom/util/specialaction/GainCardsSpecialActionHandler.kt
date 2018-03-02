package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.util.KingdomUtil

import java.util.*

object GainCardsSpecialActionHandler {

    val foolsGoldCardAction: CardAction
        get() {
            val cardAction = CardAction(CardAction.TYPE_YES_NO)
            cardAction.deck = Deck.Hinterlands
            cardAction.cardName = "Fool's Gold"
            cardAction.instructions = "Do you want to trash your Fool's Gold to gain a Gold on top of your deck?"

            return cardAction
        }

    fun getCardAction(game: Game, player: Player, card: Card): CardAction? {
        val supplyMap = game.supplyMap

        when (card.name) {
            "Border Village" -> {
                val cardAction = CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY)
                cardAction.isGainCardAction = true
                cardAction.deck = Deck.Hinterlands
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.associatedCard = card
                cardAction.instructions = "Select one of the following cards to gain and then click Done."
                val cost = game.getCardCost(card)
                for (c in supplyMap.values) {
                    if (game.getCardCost(c) < cost && !c.costIncludesPotion && game.isCardInSupply(c)) {
                        cardAction.cards.add(c)
                    }
                }
                if (cardAction.cards.size > 0) {
                    return cardAction
                }
            }
            "Cache" -> for (i in 0..1) {
                if (game.supply[Card.COPPER_ID]!! > 0) {
                    game.playerGainedCard(player, game.copperCard)
                }
            }
            "Duchy" -> {
                val duchessCard = game.kingdomCardMap["Duchess"]!!
                if (game.isCheckDuchess && game.isCardInSupply(duchessCard)) {
                    val cardAction = CardAction(CardAction.TYPE_YES_NO)
                    cardAction.isGainCardAction = true
                    cardAction.deck = Deck.Reaction
                    cardAction.cardName = "Duchess for Duchy"
                    cardAction.associatedCard = card
                    cardAction.cards.add(duchessCard)
                    cardAction.instructions = "Do you want to gain a Duchess?"
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Embassy" -> {
                var playerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                while (playerIndex != game.currentPlayerIndex) {
                    val nextPlayer = game.players[playerIndex]
                    if (game.isCardInSupply(Card.SILVER_ID)) {
                        game.playerGainedCard(nextPlayer, game.silverCard)
                        game.refreshDiscard(nextPlayer)
                    }
                    playerIndex = game.calculateNextPlayerIndex(playerIndex)
                }
            }
            "Inn" -> if (!player.discard.isEmpty()) {
                val cards = player.discard.filterTo(ArrayList()) { it.isAction }
                val cardAction = CardAction(CardAction.TYPE_CHOOSE_UP_TO)
                cardAction.isGainCardAction = true
                cardAction.deck = Deck.Hinterlands
                cardAction.cardName = card.name
                cardAction.associatedCard = card
                cardAction.buttonValue = "Done"
                cardAction.numCards = cards.size
                cardAction.cards = cards
                cardAction.instructions = "Select the Action cards from your discard pile that you want to shuffle into your deck and then click Done."
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.setPlayerInfoDialog(player, InfoDialog.getErrorDialog("There were no Action cards in your discard pile."))
                }
            } else {
                game.setPlayerInfoDialog(player, InfoDialog.getErrorDialog("Your discard pile is empty."))
            }
            "Ill-Gotten Gains" -> {
                var playerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                while (playerIndex != game.currentPlayerIndex) {
                    val nextPlayer = game.players[playerIndex]
                    if (game.isCardInSupply(Card.CURSE_ID)) {
                        game.playerGainedCard(nextPlayer, game.curseCard)
                        game.refreshDiscard(nextPlayer)
                    }
                    playerIndex = game.calculateNextPlayerIndex(playerIndex)
                }
            }
            "Mandarin" -> if (!game.treasureCardsPlayed.isEmpty() && player.userId == game.currentPlayerId) {
                val cards = HashSet(game.treasureCardsPlayed)
                if (cards.size == 1) {
                    //put all cards on top of deck since they are all the same
                    game.addHistory(player.username, " added ", KingdomUtil.getPlural(game.treasureCardsPlayed.size, "treasure card"), " from play on top of ", player.pronoun, " deck")
                    player.deck.addAll(0, game.treasureCardsPlayed)
                    game.cardsPlayed.removeAll(game.treasureCardsPlayed)
                    game.treasureCardsPlayed.clear()
                    game.refreshAllPlayersCardsPlayed()
                } else {
                    val cardAction = CardAction(CardAction.TYPE_CHOOSE_IN_ORDER)
                    cardAction.isGainCardAction = true
                    cardAction.deck = Deck.Hinterlands
                    cardAction.isHideOnSelect = true
                    cardAction.numCards = game.treasureCardsPlayed.size
                    cardAction.cardName = card.name
                    cardAction.associatedCard = card
                    cardAction.cards = game.treasureCardsPlayed
                    cardAction.buttonValue = "Done"
                    cardAction.instructions = "Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)"
                    return cardAction
                }
            }
        }

        return null
    }
}
