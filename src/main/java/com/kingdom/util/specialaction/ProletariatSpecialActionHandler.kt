package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.util.KingdomUtil

object ProletariatSpecialActionHandler {

    fun handleSpecialAction(game: Game, card: Card): IncompleteCard? {
        val player = game.currentPlayer
        var incompleteCard: IncompleteCard? = null

        when (card.name) {
            "Cattle Farm" -> {
                val topDeckCard = player!!.removeTopDeckCard()
                if (topDeckCard != null) {
                    val cardAction = CardAction(CardAction.TYPE_CHOICES)
                    cardAction.deck = Deck.Proletariat
                    cardAction.cardName = card.name
                    cardAction.associatedCard = topDeckCard
                    cardAction.cards.add(topDeckCard)
                    cardAction.instructions = "Do you want to discard the top card of your deck, or put it back?"
                    cardAction.choices.add(CardActionChoice("Discard", "discard"))
                    cardAction.choices.add(CardActionChoice("Put it back", "back"))
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "City Planner" -> if (player!!.hand.size > 0) {
                if (player.hand.size == 1) {
                    game.addHistory(player.username, " discarded 1 card")
                    game.playerDiscardedCard(player, player.hand[0])
                    player.discardCardFromHand(player.hand[0])
                } else {
                    val cardAction = CardAction(CardAction.TYPE_DISCARD_FROM_HAND)
                    cardAction.deck = Deck.Proletariat
                    cardAction.cardName = card.name
                    cardAction.cards.addAll(player.hand)
                    cardAction.numCards = 1
                    cardAction.instructions = "Select 1 card to discard and then click Done."
                    cardAction.buttonValue = "Done"
                    game.setPlayerCardAction(player, cardAction)
                }
            }
            "Fruit Merchant" -> if (!player!!.hand.isEmpty()) {
                val cardAction = CardAction(CardAction.TYPE_DISCARD_UP_TO_FROM_HAND)
                cardAction.deck = Deck.Proletariat
                cardAction.cardName = card.name
                cardAction.cards.addAll(player.hand)
                cardAction.numCards = 2
                cardAction.instructions = "Select up to 2 cards to discard to gain a fruit token per card discarded and then click Done, or just click Done if you don't want to discard a card."
                cardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, cardAction)
            }
            "Hooligans" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                val players = game.players
                for (p in players) {
                    if (!game.isCurrentPlayer(p)) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(p.userId)) {
                            incompleteCard.setPlayerActionCompleted(p.userId)
                            game.addHistory(p.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                        } else if (!p.hasMoat() && p.hand.size > 3 && !p.hasLighthouse()) {
                            val cardAction = CardAction(CardAction.TYPE_CHOOSE_CARDS)
                            cardAction.deck = Deck.Proletariat
                            cardAction.cardName = card.name
                            cardAction.cards.addAll(p.hand)
                            cardAction.numCards = 1
                            cardAction.instructions = "Choose a card from your hand. " + player!!.username + " will choose to place it on top of your deck or discard it."
                            cardAction.buttonValue = "Done"
                            game.setPlayerCardAction(p, cardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(p.userId)
                            when {
                                p.hasLighthouse() -> game.addHistory(p.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                                p.hasMoat() -> game.addHistory(p.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                                else -> game.addHistory(p.username, " had 3 or less cards")
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Rancher" -> if (!player!!.getVictoryCards().isEmpty()) {
                val cardAction = CardAction(CardAction.TYPE_CHOOSE_UP_TO)
                cardAction.deck = Deck.Proletariat
                cardAction.cardName = card.name
                cardAction.cards.addAll(player.getVictoryCards())
                cardAction.numCards = 1
                cardAction.associatedCard = card
                cardAction.instructions = "Choose a victory card to reveal from your hand to gain a cattle token or +1 Buy and then click Done, or just click Done if you don't want to reveal a card."
                cardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, cardAction)
            }
            "Refugee Camp" -> {
                while (player!!.hand.size < 5) {
                    val topCard = player.removeTopDeckCard() ?: break
                    player.addCardToHand(topCard)
                }
                game.refreshHand(player)
            }
            "Squatter" -> {
                game.addHistory(player!!.username, " returned ", KingdomUtil.getCardWithBackgroundColor(card), " to the supply")
                game.removePlayedCard(card)
                game.playerLostCard(player, card)
                game.addToSupply(card.cardId)
            }
            "Trainee" -> if (!player!!.hand.isEmpty()) {
                val cardAction = CardAction(CardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Proletariat
                cardAction.cardName = card.name
                cardAction.cards.addAll(player.hand)
                cardAction.numCards = 1
                cardAction.associatedCard = card
                cardAction.instructions = "Choose a card from your hand to return to the supply with your Trainee in order to gain an action card costing up to the combined cost of the two cards, and then click Done."
                cardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, cardAction)
            }
        }

        return incompleteCard
    }
}
