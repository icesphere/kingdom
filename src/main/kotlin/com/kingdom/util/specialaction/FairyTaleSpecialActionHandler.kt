package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil

object FairyTaleSpecialActionHandler {
    fun handleSpecialAction(game: Game, card: Card, repeatedAction: Boolean): IncompleteCard? {

        var incompleteCard: IncompleteCard? = null
        val player = game.currentPlayer

        when (card.name) {
            "Bridge Troll" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.FairyTale
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select the card you want to place a troll token on and then click Done."
                for (c in game.supplyMap.values) {
                    val numTrollTokens = game.trollTokens[card.name]!!
                    if (game.supply[c.name]!! > 0 && numTrollTokens <= 2) {
                        cardAction.cards.add(c)
                    }
                }
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                } else {
                    game.setPlayerInfoDialog(player!!, InfoDialog.getInfoDialog("There are no piles left to put the troll token on."))
                }
                if (!repeatedAction) {
                    game.removePlayedCard(card)
                    game.trashedCards.add(card)
                    game.playerLostCard(player, card)
                }
            }
            "Druid" -> if (!player!!.getVictoryCards().isEmpty()) {
                val cardAction = OldCardAction(OldCardAction.TYPE_DISCARD_UP_TO_FROM_HAND)
                cardAction.deck = Deck.FairyTale
                cardAction.cardName = card.name
                cardAction.cards.addAll(player.getVictoryCards())
                cardAction.numCards = 2
                cardAction.instructions = "Discard up to 2 victory cards. +2 Coins per card discarded. Select the Cards you want to discard and then click Done."
                cardAction.buttonValue = "Done"
                game.setPlayerCardAction(player, cardAction)
            } else {
                player.drawCards(2)
                game.addHistory(player.username, " did not have any victory cards and got +2 cards.")
            }
            "Lost Village" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.FairyTale
                cardAction.cardName = "Lost Village 1"
                cardAction.instructions = "Choose one: +2 Actions, or +1 Action and set aside cards until you choose to draw one."
                cardAction.choices.add(CardActionChoice("+2 Actions", "actions"))
                cardAction.choices.add(CardActionChoice("+1 Action and draw", "draw"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Magic Beans" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.FairyTale
                cardAction.cardName = card.name
                cardAction.cards.add(card)
                cardAction.instructions = "Do you want to trash your Magic Beans, or return it to the supply?"
                cardAction.choices.add(CardActionChoice("Trash", "trash"))
                cardAction.choices.add(CardActionChoice("Return to Supply", "supply"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Master Huntsman" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (otherPlayer in game.players) {
                    if (otherPlayer.userId != game.currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(otherPlayer.userId)) {
                            incompleteCard.setPlayerActionCompleted(otherPlayer.userId)
                            game.addHistory(otherPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                        } else if (!otherPlayer.hasMoat() && !otherPlayer.hasLighthouse() && otherPlayer.hand.size >= 4 && !otherPlayer.actionCards.isEmpty()) {
                            val cardAction = OldCardAction(OldCardAction.TYPE_DISCARD_FROM_HAND)
                            cardAction.deck = Deck.FairyTale
                            cardAction.cardName = card.name
                            cardAction.buttonValue = "Done"
                            cardAction.numCards = 1
                            cardAction.instructions = "Select an action card to discard."
                            cardAction.cards.addAll(otherPlayer.actionCards)
                            game.setPlayerCardAction(otherPlayer, cardAction)
                        } else {
                            incompleteCard.setPlayerActionCompleted(otherPlayer.userId)
                            when {
                                otherPlayer.hasLighthouse() -> game.addHistory(otherPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR))
                                otherPlayer.hasMoat() -> game.addHistory(otherPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR))
                                otherPlayer.hand.size < 4 -> game.addHistory(otherPlayer.username, " had less than 4 cards")
                                else -> game.addHistory(otherPlayer.username, " did not have any action cards")
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Quest" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.FairyTale
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select the card you want to name for your Quest."
                for (c in game.cardMap.values) {
                    if (c.isAction || c.isVictory) {
                        cardAction.cards.add(c)
                    }
                }
                cardAction.associatedCard = card
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Sorceress" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.FairyTale
                cardAction.cardName = card.name
                cardAction.instructions = "Choose first effect to apply."
                cardAction.choices.add(CardActionChoice("+2 Cards", "cards"))
                cardAction.choices.add(CardActionChoice("+2 Actions", "actions"))
                cardAction.choices.add(CardActionChoice("+2 Coins", "coins"))
                cardAction.choices.add(CardActionChoice("+2 Buys", "buys"))
                cardAction.choices.add(CardActionChoice("Trash 2 Cards", "trash"))
                cardAction.phase = 1
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Tinker" -> player!!.isPlayedTinker = true
        }

        return incompleteCard
    }
}
