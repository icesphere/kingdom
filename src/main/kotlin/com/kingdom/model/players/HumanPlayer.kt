package com.kingdom.model.players

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.*

class HumanPlayer(user: User, game: Game) : Player(user, game) {

    override fun takeTurn() {}

    private fun addAction(action: Action, isAttackAction: Boolean = !isYourTurn) {
        action.isAttackAction = isAttackAction

        actionsQueue.add(action)

        if (isYourTurn && currentAction == null) {
            resolveActions()
        }
    }

    override fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String) {
        addAction(TrashCardsFromHand(numCardsToTrash, text, true))
    }

    override fun optionallyTrashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String) {
        addAction(TrashCardsFromHandForBenefit(card, numCardsToTrash, text, true))
    }

    override fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String) {
        addAction(TrashCardsFromHandForBenefit(card, numCardsToTrash, text, false))
    }

    override fun optionallyDiscardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String) {
        addAction(DiscardCardsFromHandForBenefit(card, numCardsToDiscard, text, true))
    }

    override fun discardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String) {
        addAction(DiscardCardsFromHandForBenefit(card, numCardsToDiscard, text))
    }

    override fun makeChoice(card: ChoiceActionCard, vararg choices: Choice) {
        addAction(ChoiceAction(card, *choices))
    }

    override fun makeChoice(card: ChoiceActionCard, text: String, vararg choices: Choice) {
        addAction(ChoiceAction(card, text, *choices))
    }

    override fun trashCardFromHand(optional: Boolean) {
        if (optional) {
            addAction(TrashCardsFromHand(1, "You may trash a card from your hand", true))
        } else {
            addAction(TrashCardsFromHand(1, "Trash a card from your hand", false))
        }
    }

    override fun acquireFreeCard(maxCost: Int?) {
        addAction(FreeCardFromSupply(maxCost, "Acquire a free card from the supply costing up to " + maxCost))
    }

    override fun acquireFreeCardToTopOfDeck(maxCost: Int?) {
        addAction(FreeCardFromSupply(maxCost, "Acquire a free card from the supply to the top of your deck costing up to " + maxCost, CardLocation.Deck))
    }

    override fun acquireFreeCardToHand(maxCost: Int?) {
        addAction(FreeCardFromSupply(maxCost, "Acquire a free card from the supply to your hand costing up to " + maxCost, CardLocation.Hand))
    }

    override fun acquireFreeCardOfTypeToHand(maxCost: Int?, cardType: CardType) {
        addAction(FreeCardFromSupply(maxCost, "Acquire a free card from the supply to your hand costing up to " + maxCost, CardLocation.Hand, cardType))
    }

    override fun yesNoChoice(choiceActionCard: ChoiceActionCard, text: String) {
        addAction(YesNoAbilityAction(choiceActionCard, text))
    }

    override fun drawCardsAndPutSomeBackOnTop(cardsToDraw: Int, cardsToPutBack: Int) {
        addAction(DrawCardsAndPutSomeBackOnTopOfDeck(cardsToDraw, cardsToPutBack))
    }

    override fun putCardsOnTopOfDeckInAnyOrder(cards: List<Card>) {
        addAction(PutCardsOnTopOfDeckInAnyOrder(cards))
    }

    override fun discardCardsFromHand(cards: Int) {
        val lastAction = lastAction
        if (lastAction != null && lastAction is DiscardCardsFromHand) {
            val discardCardsFromHand = lastAction
            discardCardsFromHand.numCardsToDiscard = discardCardsFromHand.numCardsToDiscard + cards
            discardCardsFromHand.setTextFromNumberOfCardsToDiscard()
        } else {
            addAction(DiscardCardsFromHand(cards))
        }
    }

    private val lastAction: Action?
        get() {
            if (!actionsQueue.isEmpty()) {
                return actionsQueue[actionsQueue.size - 1]
            }
            return null
        }

    override fun addCardAction(card: CardActionCard, text: String) {
        addAction(CardAction(card, text))
    }

    override fun addCardFromDiscardToTopOfDeck(maxCost: Int?) {
        addAction(CardFromDiscardToTopOfDeck(maxCost))
    }

    override fun addCardFromHandToTopOfDeck(cardFilter: ((Card) -> Boolean)?) {
        addAction(CardFromHandToTopOfDeck(cardFilter))
    }

    override fun waitForOtherPlayersToResolveActions() {
        addAction(WaitForOtherPlayersActions(this))
    }

    override fun waitForOtherPlayersToResolveActionsWithResults(resultHandler: ActionResultHandler) {
        addAction(WaitForOtherPlayersActionsWithResults(this, resultHandler))
    }

    override fun selectCardsToTrashFromDeck(cardsThatCanBeTrashed: List<Card>, numCardsToTrash: Int, optional: Boolean) {
        addAction(SelectCardsToTrashFromDeck(cardsThatCanBeTrashed, numCardsToTrash, optional))
    }
}
