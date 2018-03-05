package com.kingdom.util.cardaction

import com.kingdom.model.*
import com.kingdom.model.cards.Deck
import com.kingdom.util.KingdomUtil

object CardActionHandler {
    fun handleSubmittedCardAction(game: Game, player: Player, selectedCardIds: List<Int>, yesNoAnswer: String?, choice: String?, numberChosen: Int) {

        player.isShowCardAction = false

        val supplyMap = game.supplyMap
        val cardAction = player.oldCardAction
        val type = cardAction!!.type
        var incompleteCard: IncompleteCard? = null

        when {
            cardAction.isDiscard -> incompleteCard = DiscardCardsHandler.handleCardAction(game, player, cardAction, selectedCardIds)
            type == OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY || type == OldCardAction.TYPE_GAIN_UP_TO_FROM_SUPPLY || type == OldCardAction.TYPE_GAIN_CARDS || type == OldCardAction.TYPE_GAIN_CARDS_UP_TO -> GainCardsHandler.handleCardAction(game, player, cardAction, selectedCardIds)
            type == OldCardAction.TYPE_TRASH_CARDS_FROM_HAND || type == OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND -> incompleteCard = TrashCardsHandler.handleCardAction(game, player, cardAction, selectedCardIds)
            type == OldCardAction.TYPE_GAIN_CARDS_INTO_HAND_FROM_SUPPLY -> {
                selectedCardIds
                        .mapNotNull { supplyMap[it] }
                        .forEach { game.playerGainedCardToHand(player, it) }
                game.refreshPlayingArea(player)
            }
            type == OldCardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK -> when {
                selectedCardIds.size > 1 && cardAction.cardName == "Ghost Ship" -> {
                    val reorderCardAction = OldCardAction(OldCardAction.TYPE_CHOOSE_IN_ORDER)
                    reorderCardAction.deck = Deck.Seaside
                    reorderCardAction.isHideOnSelect = true
                    reorderCardAction.numCards = selectedCardIds.size
                    reorderCardAction.cardName = cardAction.cardName
                    for (selectedCardId in selectedCardIds) {
                        val card = supplyMap[selectedCardId]!!
                        player.removeCardFromHand(card)
                        reorderCardAction.cards.add(card)
                    }
                    reorderCardAction.buttonValue = "Done"
                    reorderCardAction.instructions = "Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)"
                    game.setPlayerCardAction(player, reorderCardAction)
                }
                cardAction.cardName == "Bureaucrat" -> {
                    val card = supplyMap[selectedCardIds[0]]!!
                    game.addHistory(player.username, " added 1 Victory card on top of ", player.pronoun, " deck")
                    player.putCardFromHandOnTopOfDeck(card)
                }
                else -> {
                    selectedCardIds
                            .map { player.getCardFromHandById(it) }
                            .forEach { player.putCardFromHandOnTopOfDeck(it!!) }
                    game.addHistory(player.username, " added ", KingdomUtil.getPlural(selectedCardIds.size, "card"), " on top of ", player.pronoun, " deck")
                }
            }
            type == OldCardAction.TYPE_CHOOSE_CARDS || type == OldCardAction.TYPE_SETUP_LEADERS -> incompleteCard = ChooseCardsHandler.handleCardAction(game, player, cardAction, selectedCardIds)
            type == OldCardAction.TYPE_YES_NO -> incompleteCard = YesNoHandler.handleCardAction(game, player, cardAction, yesNoAnswer!!)
            type == OldCardAction.TYPE_CHOICES -> incompleteCard = ChoicesHandler.handleCardAction(game, player, cardAction, choice!!)
            type == OldCardAction.TYPE_CHOOSE_IN_ORDER -> incompleteCard = ChooseInOrderHandler.handleCardAction(game, player, cardAction, selectedCardIds)
            type == OldCardAction.TYPE_CHOOSE_UP_TO -> incompleteCard = ChooseUpToHandler.handleCardAction(game, player, cardAction, selectedCardIds)
            type == OldCardAction.TYPE_CHOOSE_NUMBER_BETWEEN || type == OldCardAction.TYPE_CHOOSE_EVEN_NUMBER_BETWEEN -> ChooseNumberBetweenHandler.handleCardAction(game, player, cardAction, numberChosen)
        }

        if (cardAction.isGainCardAction) {
            game.finishedGainCardAction(player, cardAction)
        }

        if (game.hasIncompleteCard()) {
            game.incompleteCard!!.actionFinished(player)
        }

        game.refreshHandArea(player)
        game.refreshCardsBought(player)

        when {
            !player.isShowCardAction && !player.extraOldCardActions.isEmpty() -> game.setPlayerCardAction(player, player.extraOldCardActions.remove())
            !player.isShowCardAction && cardAction.isGainCardAction && game.hasUnfinishedGainCardActions() -> when {
                !cardAction.associatedCard!!.gainOldCardActions.isEmpty() -> game.setPlayerGainCardAction(player, cardAction.associatedCard!!)
                else -> game.setPlayerGainCardAction(player, game.cardWithUnfinishedGainCardActions)
            }
        }

        if (cardAction.isGainCardAfterBuyAction) {
            game.playerGainedCard(player, cardAction.associatedCard!!)
        }

            //check for throne room/king's court/golem actions

        //check for throne room/king's court/golem actions

        //check for throne room/king's court/golem actions

        //check for throne room/king's court/golem actions
        if (!game.hasIncompleteCard() && !game.currentPlayer!!.isShowCardAction) {
            if (!game.repeatedActions.isEmpty()) {
                game.playRepeatedAction(game.currentPlayer!!, false)
            } else if (!game.golemActions.isEmpty()) {
                game.playGolemActionCard(game.currentPlayer)
            }
        }

        if (!game.hasIncompleteCard() && !player.isShowCardAction && !game.isCurrentPlayer(player) && game.playersWithCardActions.isEmpty()) {
            if (game.currentPlayer!!.isShowCardAction && game.currentPlayer!!.oldCardAction!!.isWaitingForPlayers) {
                game.closeCardActionDialog(game.currentPlayer!!)
                game.closeLoadingDialog(game.currentPlayer!!)
            }
        }

        if (incompleteCard != null && incompleteCard.isEndTurn) {
            game.isEndingTurn = false
            game.endPlayerTurn(player, false)
        }
    }
}
