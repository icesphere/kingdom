package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardColor
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.Curse
import com.kingdom.util.KingdomUtil

import java.util.ArrayList
import java.util.Collections

object SalvationSpecialActionHandler {
    fun handleSpecialAction(game: OldGame, card: Card): IncompleteCard? {

        var incompleteCard: IncompleteCard? = null
        val player = game.currentPlayer

        when (card.name) {
            "Alms" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                cardAction.deck = Deck.Salvation
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select a treasure card to trash."
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.treasureCards)
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("You don't have any treasure cards to trash."))
                }
            }
            "Archbishop" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Salvation
                cardAction.cardName = card.name
                cardAction.instructions = "Choose one: +2 Actions; remove 1 sin; or each other player gains 1 sin."
                cardAction.choices.add(CardActionChoice("+2 Actions", "actions"))
                cardAction.choices.add(CardActionChoice("Remove 1 Sin", "remove"))
                cardAction.choices.add(CardActionChoice("Others +1 Sin", "sins"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Assassin" -> {
                incompleteCard = MultiPlayerIncompleteCard(card.name, game, false)
                for (otherPlayer in game.players) {
                    if (otherPlayer.userId != game.currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(otherPlayer.userId)) {
                            incompleteCard.setPlayerActionCompleted(otherPlayer.userId)
                            game.addHistory(otherPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                        } else if (!otherPlayer.hasMoat() && !otherPlayer.hasLighthouse()) {
                            val cardAction = OldCardAction(OldCardAction.TYPE_TRASH_CARDS_FROM_HAND)
                            cardAction.deck = Deck.Salvation
                            cardAction.cardName = card.name
                            cardAction.buttonValue = "Done"
                            cardAction.numCards = 1
                            cardAction.instructions = "Select an attack card to trash."
                            for (c in otherPlayer.actionCards) {
                                if (c.isAttack) {
                                    cardAction.cards.add(c)
                                }
                            }
                            if (!cardAction.cards.isEmpty()) {
                                game.setPlayerCardAction(otherPlayer, cardAction)
                            } else {
                                incompleteCard.setPlayerActionCompleted(otherPlayer.userId)
                                game.addHistory(otherPlayer.username, " did not have any attack cards")
                            }
                        } else {
                            incompleteCard.setPlayerActionCompleted(otherPlayer.userId)
                            if (otherPlayer.hasLighthouse()) {
                                game.addHistory(otherPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                            } else {
                                game.addHistory(otherPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet()
            }
            "Baptistry" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Salvation
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Choose a card."
                cardAction.cards.addAll(game.cardMap.values)
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
            "Catacombs" -> {
                val cards = ArrayList<Card>()
                Collections.shuffle(player!!.discard)
                game.refreshDiscard(player)
                if (player.discard.size <= 4) {
                    cards.addAll(player.discard)
                } else {
                    cards.addAll(player.discard.subList(player.discard.size - 4, player.discard.size))
                }
                if (cards.isEmpty()) {
                    game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("Your discard pile was empty."))
                } else {
                    game.addHistory("Catacombs revealed ", KingdomUtil.groupCards(cards, true))
                    if (cards.size == 1) {
                        game.playerGainedCardToHand(player, cards[0], false)
                        player.discard.removeLastOccurrence(cards[0])
                    } else if (cards.size > 0) {
                        val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                        cardAction.deck = Deck.Salvation
                        cardAction.cardName = card.name
                        cardAction.buttonValue = "Done"
                        cardAction.numCards = 1
                        cardAction.cards = KingdomUtil.uniqueCardList(cards)
                        cardAction.instructions = "Select a card to gain from your discard pile."
                        game.setPlayerCardAction(player, cardAction)
                    }
                }
            }
            "Edict" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                cardAction.deck = Deck.Salvation
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Choose an action card for the Edict."
                for (c in game.cardMap.values) {
                    if (c.isAction) {
                        cardAction.cards.add(c)
                    }
                }
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Graverobber" -> {
                incompleteCard = SinglePlayerIncompleteCard(card.name, game)
                for (otherPlayer in game.players) {
                    if (otherPlayer.userId != game.currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(otherPlayer.userId)) {
                            game.addHistory(otherPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                        } else if (!otherPlayer.hasMoat() && !otherPlayer.hasLighthouse() && !otherPlayer.discard.isEmpty()) {
                            var randomIndex1 = 0
                            var randomIndex2 = -1
                            if (otherPlayer.discard.size > 1) {
                                randomIndex1 = KingdomUtil.getRandomNumber(0, otherPlayer.discard.size - 1)
                                randomIndex2 = KingdomUtil.getRandomNumber(0, otherPlayer.discard.size - 1)
                                while (randomIndex2 == randomIndex1) {
                                    randomIndex2 = KingdomUtil.getRandomNumber(0, otherPlayer.discard.size - 1)
                                }
                            }
                            for (i in 1..2) {
                                var revealedCard: Card? = null
                                var index = 0
                                if (i == 1) {
                                    revealedCard = otherPlayer.discard[randomIndex1]
                                    index = randomIndex1
                                } else if (i == 2 && randomIndex2 >= 0) {
                                    revealedCard = otherPlayer.discard[randomIndex2]
                                    index = randomIndex2
                                }
                                if (revealedCard != null) {
                                    val nextOldCardAction: OldCardAction
                                    if (revealedCard.isTreasure) {
                                        nextOldCardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                                        nextOldCardAction.instructions = "The graverobber revealed this treasure card from " + otherPlayer.username + "'s discard pile. Do you want to gain this card?"
                                    } else {
                                        nextOldCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_CARDS)
                                        nextOldCardAction.numCards = 0
                                        nextOldCardAction.buttonValue = "Continue"
                                        nextOldCardAction.instructions = "The graverobber revealed this non-treasure card from " + otherPlayer.username + "'s discard pile. Click Continue."
                                    }
                                    nextOldCardAction.deck = Deck.Salvation
                                    nextOldCardAction.playerId = otherPlayer.userId
                                    nextOldCardAction.cardName = card.name
                                    nextOldCardAction.cards.add(revealedCard)
                                    incompleteCard.extraOldCardActions.add(nextOldCardAction)
                                }
                            }
                        } else {
                            when {
                                otherPlayer.hasLighthouse() -> game.addHistory(otherPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                                otherPlayer.hasMoat() -> game.addHistory(otherPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                                else -> game.addHistory(otherPlayer.username, "'s discard pile was empty")
                            }
                        }
                    }
                }
                if (!incompleteCard.extraOldCardActions.isEmpty()) {
                    val cardAction = incompleteCard.extraOldCardActions.remove()
                    cardAction.deck = Deck.Salvation
                    game.setPlayerCardAction(player!!, cardAction)
                }
            }
            "Indulgence" -> {
                for (otherPlayer in game.players) {
                    if (otherPlayer.userId != game.currentPlayerId) {
                        otherPlayer.addSins(-1)
                    }
                }
                game.refreshAllPlayersPlayers()
                game.addHistory("All other players removed one sin")
            }
            "Inquisitor" -> {
                for (otherPlayer in game.players) {
                    if (otherPlayer.userId != game.currentPlayerId) {
                        if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(otherPlayer.userId)) {
                            game.addHistory(otherPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", CardColor.VictoryReaction))
                        } else if (!otherPlayer.hasMoat() && !otherPlayer.hasLighthouse()) {
                            if (otherPlayer.sins >= 2) {
                                val cursesInSupply = game.supply[Curse.NAME]!!
                                when {
                                    cursesInSupply >= 2 -> {
                                        otherPlayer.addSins(-2)
                                        game.addHistory(otherPlayer.username, " removed 2 sins and gained 2 ", KingdomUtil.getWordWithBackgroundColor("Curse", CardColor.Curse), " cards from the Inquisitor")
                                        game.playerGainedCard(otherPlayer, game.curseCard)
                                        game.playerGainedCard(otherPlayer, game.curseCard)
                                    }
                                    cursesInSupply == 1 -> {
                                        otherPlayer.addSins(-1)
                                        game.addHistory(otherPlayer.username, " removed 1 sin and gained 1 ", KingdomUtil.getWordWithBackgroundColor("Curse", CardColor.Curse), " card from the Inquisitor")
                                        game.playerGainedCard(otherPlayer, game.curseCard)
                                    }
                                    else -> {
                                        otherPlayer.addSins(1)
                                        game.addHistory(otherPlayer.username, " gained a sin from the Inquisitor")
                                    }
                                }
                            } else {
                                otherPlayer.addSins(1)
                                game.addHistory(otherPlayer.username, " gained a sin from the Inquisitor")
                            }
                            game.refreshHandArea(otherPlayer)
                        } else {
                            if (otherPlayer.hasLighthouse()) {
                                game.addHistory(otherPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", CardColor.ActionDuration))
                            } else {
                                game.addHistory(otherPlayer.username, " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", CardColor.ActionReaction))
                            }
                        }
                    }
                }
                game.refreshAllPlayersPlayers()
            }
            "Mendicant" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_UP_TO)
                cardAction.deck = Deck.Salvation
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Select a card to gain from the trash pile and then click Done, or just click Done if you do not want to gain a card."
                cardAction.cards = KingdomUtil.uniqueCardList(game.trashedCards)
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player!!, cardAction)
                } else {
                    game.setPlayerInfoDialog(player!!, InfoDialog.getInfoDialog("The trash pile is empty."))
                }
            }
            "Scriptorium" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_DISCARD_FROM_HAND)
                cardAction.deck = Deck.Salvation
                cardAction.cardName = card.name
                cardAction.buttonValue = "Done"
                cardAction.numCards = 1
                cardAction.instructions = "Chose an action card to discard from your hand."
                cardAction.cards = KingdomUtil.uniqueCardList(player!!.actionCards)
                if (cardAction.cards.size > 0) {
                    game.setPlayerCardAction(player, cardAction)
                } else {
                    game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("You do not have any action cards in your hand."))
                }
            }
        }

        return incompleteCard
    }
}
