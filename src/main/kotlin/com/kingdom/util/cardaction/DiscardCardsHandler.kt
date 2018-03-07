package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil

object DiscardCardsHandler {
    fun handleCardAction(game: Game, player: Player, oldCardAction: OldCardAction, selectedCardNames: List<String>): IncompleteCard? {

        var incompleteCard: IncompleteCard? = null

        if (selectedCardNames.isEmpty()) {
            game.addHistory(player.username, " did not discard a card")
        } else {
            game.addHistory(player.username, " discarded ", KingdomUtil.getPlural(selectedCardNames.size, "card"))
        }

        for (selectedCardName in selectedCardNames) {
            val selectedCard = game.cardMap[selectedCardName]!!
            if (oldCardAction.type == OldCardAction.TYPE_DISCARD_UP_TO) {
                player.addCardToDiscard(selectedCard)
                game.playerDiscardedCard(player, selectedCard)
            } else {
                player.discardCardFromHand(selectedCardName)
                game.playerDiscardedCard(player, selectedCard)
            }
        }

        when (oldCardAction.cardName) {
            "Cartographer" -> {
                val cards = oldCardAction.cards
                selectedCardNames
                        .map { game.cardMap[it] }
                        .forEach { cards.remove(it) }

                if (cards.isNotEmpty()) {
                    if (cards.size == 1) {
                        player.addCardToTopOfDeck(cards[0])
                    } else {
                        val chooseOrderCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER).apply {
                            deck = Deck.Hinterlands
                            isHideOnSelect = true
                            numCards = cards.size
                            cardName = oldCardAction.cardName
                            this.cards = cards
                            buttonValue = "Done"
                            instructions = "Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)"
                        }
                        game.setPlayerCardAction(player, chooseOrderCardAction)
                    }
                }
            }
            "Cellar" -> player.drawCards(selectedCardNames.size)
            "Druid" -> player.addCoins(2 * selectedCardNames.size)
            "Fruit Merchant" -> if (selectedCardNames.isNotEmpty()) {
                player.addFruitTokens(selectedCardNames.size)
                game.addHistory(player.username, " gained ", KingdomUtil.getPlural(selectedCardNames.size, "fruit token"))
            }
            "Hamlet" -> {
                player.addActions(1)
                game.refreshAllPlayersCardsPlayed()
                game.addHistory(player.username, " gained +1 Action")
                if (player.hand.isEmpty()) {
                    game.addHistory(player.username, " did not have any cards in ", player.pronoun, " hand to discard for +1 Buy")
                } else {
                    incompleteCard = SinglePlayerIncompleteCard(oldCardAction.cardName, game)
                    game.addNextAction("discard for buy")
                }
            }
            "Hamlet2" -> {
                player.addBuys(1)
                game.refreshAllPlayersCardsBought()
                game.addHistory(player.username, " gained +1 Buy")
            }
            "Scriptorium" -> {
                val selectedCard = game.cardMap[selectedCardNames[0]]!!
                game.playerGainedCard(player, selectedCard)
            }
            "Secret Chamber" -> player.addCoins(selectedCardNames.size)
            "Stables" -> if (!selectedCardNames.isEmpty()) {
                game.addHistory(player.username, " gained +3 Cards and +1 Action")
                player.drawCards(3)
                player.addActions(1)
                game.refreshAllPlayersCardsPlayed()
            }
            "Vault" -> {
                incompleteCard = MultiPlayerIncompleteCard(oldCardAction.cardName, game, false)
                player.addCoins(selectedCardNames.size)
                game.addHistory(player.username, " gained +", KingdomUtil.getPlural(selectedCardNames.size, "Coin"), " from playing ", KingdomUtil.getWordWithBackgroundColor("Vault", Card.ACTION_COLOR), "")

                for (otherPlayer in game.players) {
                    if (otherPlayer.userId != game.currentPlayerId) {
                        if (otherPlayer.hand.size >= 1) {
                            val yesNoCardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                            yesNoCardAction.deck = Deck.Prosperity
                            yesNoCardAction.cardName = "Vault"
                            when {
                                otherPlayer.hand.size == 1 -> {
                                    yesNoCardAction.numCards = 1
                                    yesNoCardAction.instructions = "Do you want to discard the card in your hand?"
                                }
                                else -> {
                                    yesNoCardAction.numCards = 2
                                    yesNoCardAction.instructions = "Do you want to discard two cards and draw one card?"
                                }
                            }
                            game.setPlayerCardAction(otherPlayer, yesNoCardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(otherPlayer.userId)
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Vault2" -> player.drawCards(1)
        }

        return incompleteCard
    }
}
