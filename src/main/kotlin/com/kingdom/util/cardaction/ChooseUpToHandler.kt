package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil
import java.util.*

object ChooseUpToHandler {
    fun handleCardAction(game: OldGame, player: OldPlayer, oldCardAction: OldCardAction, selectedCardNames: List<String>): IncompleteCard? {

        var incompleteCard: IncompleteCard? = null

        val cardMap = game.cardMap
        val players = game.players

        when (oldCardAction.cardName) {
            "Ambassador" -> {
                for (selectedCardName in selectedCardNames) {
                    val card = cardMap[selectedCardName]!!
                    player.removeCardFromHand(card)
                    game.playerLostCard(player, card)
                    game.addToSupply(card.name)
                    game.addHistory(player.username, " added ", KingdomUtil.getArticleWithCardName(card), " to the supply")
                }
                val selectedCard = oldCardAction.cards[0]
                var playerIndex = game.calculateNextPlayerIndex(game.currentPlayerIndex)
                while (playerIndex != game.currentPlayerIndex) {
                    val nextPlayer = players[playerIndex]
                    if (game.isCheckEnchantedPalace && game.revealedEnchantedPalace(nextPlayer.userId)) {
                        game.addHistory(nextPlayer.username, " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE))
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        if (game.isCardInSupply(selectedCard)) {
                            game.playerGainedCard(nextPlayer, selectedCard)
                            game.refreshDiscard(nextPlayer)
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
            "Inn" -> if (selectedCardNames.isNotEmpty()) {
                val cards = ArrayList<Card>()
                for (selectedCardId in selectedCardNames) {
                    val selectedCard = cardMap[selectedCardId]!!
                    player.discard.remove(selectedCard)
                    cards.add(selectedCard)
                }
                player.deck.addAll(cards)
                player.shuffleDeck()
                game.refreshDiscard(player)
                game.addHistory(player.username, " shuffled ", KingdomUtil.getPlural(selectedCardNames.size, " Action card"), " into ", player.pronoun, " deck")
            }
            "King's Court" -> if (selectedCardNames.isNotEmpty()) {
                val actionCard = player.getCardFromHandById(selectedCardNames[0])!!
                val cardCopy: Card
                if (game.isCheckQuest && actionCard.name == "Quest") {
                    cardCopy = Card(actionCard)
                    game.copiedPlayedCard = true
                } else {
                    cardCopy = actionCard
                }
                val firstAction = RepeatedAction(cardCopy)
                firstAction.isFirstAction = true
                val extraAction = RepeatedAction(cardCopy)
                game.repeatedActions.push(extraAction)
                game.repeatedActions.push(extraAction)
                game.repeatedActions.push(firstAction)
                if (actionCard.isDuration) {
                    game.durationCardsPlayed.add(game.kingsCourtCard!!)
                }
                game.addHistory(player.username, " used ", KingdomUtil.getWordWithBackgroundColor("King's Court", Card.ACTION_COLOR), " on ", KingdomUtil.getArticleWithCardName(actionCard))
                game.playRepeatedAction(player, true)
            } else {
                game.addHistory(player.username, " chose not to play an action with ", KingdomUtil.getWordWithBackgroundColor("King's Court", Card.ACTION_COLOR))
            }
            "Mendicant" -> if (selectedCardNames.isNotEmpty()) {
                val selectedCard = cardMap[selectedCardNames[0]]!!
                game.trashedCards.remove(selectedCard)
                game.playerGainedCard(player, selectedCard, false)
            } else {
                game.addHistory(player.username, " chose to not gain a card from the trash pile")
            }
            "Museum" -> {
                if (selectedCardNames.isNotEmpty()) {
                    val selectedCard = cardMap[selectedCardNames[0]]!!
                    player.removeCardFromHand(selectedCard)
                    player.museumCards.add(selectedCard)
                }
                if (player.museumCards.size >= 4) {
                    val museumCardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                    museumCardAction.deck = Deck.Fan
                    museumCardAction.cardName = "Museum Trash Cards"
                    museumCardAction.instructions = "Do you want to trash 4 cards from your Museum mat to gain a Prize and a Duchy?"
                    game.setPlayerCardAction(player, museumCardAction)
                }
            }
            "Rancher" -> if (selectedCardNames.isNotEmpty()) {
                val selectedCard = cardMap[selectedCardNames[0]]!!
                game.addHistory(player.username, " revealed ", KingdomUtil.getArticleWithCardName(selectedCard))
                val choicesCardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                choicesCardAction.deck = Deck.Proletariat
                choicesCardAction.cardName = oldCardAction.cardName
                choicesCardAction.instructions = "Choose one: +1 cattle token or +1 Buy."
                choicesCardAction.choices.add(CardActionChoice("cattle token", "cattle"))
                choicesCardAction.choices.add(CardActionChoice("+1 Buy", "buy"))
                game.setPlayerCardAction(player, choicesCardAction)
            }
            "Storybook" -> if (selectedCardNames.isNotEmpty()) {
                for (selectedCardId in selectedCardNames) {
                    val selectedCard = cardMap[selectedCardId]!!
                    player.removeCardFromHand(selectedCard)
                    oldCardAction.associatedCard!!.associatedCards.add(selectedCard)
                    player.addCoins(1)
                }
                game.addHistory(player.username, " added ", KingdomUtil.getPlural(selectedCardNames.size, "card"), " under ", KingdomUtil.getCardWithBackgroundColor(oldCardAction.associatedCard!!), " and gained ", "+", KingdomUtil.getPlural(selectedCardNames.size, "coin"))
            }
            "Treasury", "Alchemist", "Herbalist", "Walled Village", "Scheme" -> {
                incompleteCard = SinglePlayerIncompleteCard(oldCardAction.cardName, game)

                for (selectedCardId in selectedCardNames) {
                    val card = cardMap[selectedCardId]!!
                    game.removePlayedCard(card)
                    player.addCardToTopOfDeck(card)
                }

                when (oldCardAction.cardName) {
                    "Treasury", "Alchemist", "Walled Village" ->
                        for (card in oldCardAction.cards) {
                            card.isAutoSelect = false
                        }
                }

                if (selectedCardNames.isNotEmpty()) {
                    val typeAdded: String = when (oldCardAction.cardName) {
                        "Herbalist" -> KingdomUtil.getPlural(selectedCardNames.size, "Treasure Card")
                        "Scheme" -> KingdomUtil.getPlural(selectedCardNames.size, "Action Card")
                        else -> KingdomUtil.getPlural(selectedCardNames.size, oldCardAction.cardName + " card")
                    }
                    game.addHistory(player.username, " added ", typeAdded, " to the top of ", player.pronoun, " deck")
                }

                incompleteCard.isEndTurn = true
            }
        }

        return incompleteCard
    }
}
