package com.kingdom.model.players

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.actions.*

class HumanPlayer(user: User, game: Game) : Player(user, game) {

    override fun takeTurn() {}

    private fun addAction(action: Action) {
        actionsQueue.add(action)

        if (currentAction == null) {
            resolveActions()
        }
    }

    override fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(TrashCardsFromHand(numCardsToTrash, text, true, cardActionableExpression))
    }

    override fun optionallyTrashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?, info: Any?) {
        addAction(TrashCardsFromHandForBenefit(card, numCardsToTrash, text, true, cardActionableExpression, info))
    }

    override fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?, info: Any?) {
        addAction(TrashCardsFromHandForBenefit(card, numCardsToTrash, text, false, cardActionableExpression, info))
    }

    override fun optionallyDiscardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, info: Any?) {
        addAction(DiscardCardsFromHandForBenefit(card, numCardsToDiscard, text, true, null, info))
    }

    override fun discardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(DiscardCardsFromHandForBenefit(card, numCardsToDiscard, text, false, cardActionableExpression))
    }

    override fun makeChoice(card: ChoiceActionCard, vararg choices: Choice) {
        addAction(ChoiceAction(card, *choices))
    }

    override fun makeChoice(card: ChoiceActionCard, text: String, vararg choices: Choice) {
        addAction(ChoiceAction(card, text, *choices))
    }

    override fun makeChoiceWithInfo(card: ChoiceActionCard, text: String, info: Any, vararg choices: Choice) {
        addAction(ChoiceAction(card, text, info, *choices))
    }

    override fun trashCardsFromHand(numCardsToTrash: Int, optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)?) {
        if (optional) {
            addAction(TrashCardsFromHand(numCardsToTrash, "You may trash a card from your hand", true, cardActionableExpression))
        } else {
            addAction(TrashCardsFromHand(numCardsToTrash, "", false, cardActionableExpression))
        }
    }

    override fun trashCardFromSupply(optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)?) {
        if (optional) {
            addAction(TrashCardsFromSupply(1, true, cardActionableExpression))
        } else {
            addAction(TrashCardsFromSupply(1, false, cardActionableExpression))
        }
    }

    override fun gainCardFromTrash(optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(ChooseCardToGainFromTrash(game.trashedCards, optional, cardActionableExpression))
    }

    override fun chooseSupplyCardToGain(cardActionableExpression: ((card: Card) -> Boolean)?, text: String?, destination: CardLocation, optional: Boolean) {
        val actionText = when {
            text != null -> text
            else -> "Gain a free card from the supply"
        }
        addAction(FreeCardFromSupply(actionText, cardActionableExpression, destination, optional))
    }

    override fun chooseSupplyCardToGainForBenefit(text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(FreeCardFromSupplyForBenefit(freeCardFromSupplyForBenefitActionCard, text, cardActionableExpression))
    }

    override fun yesNoChoice(choiceActionCard: ChoiceActionCard, text: String, info: Any?) {
        addAction(YesNoAbilityAction(choiceActionCard, text, info))
    }

    override fun drawCardsAndPutSomeBackOnTop(cardsToDraw: Int, cardsToPutBack: Int) {
        addAction(DrawCardsAndPutSomeBackOnTopOfDeck(cardsToDraw, cardsToPutBack))
    }

    override fun putCardsOnTopOfDeckInAnyOrder(cards: List<Card>) {
        addAction(PutCardsOnTopOfDeckInAnyOrder(cards))
    }

    override fun discardCardsFromHand(numCardsToDiscard: Int, optional: Boolean) {
        val numCards = if (hand.size < numCardsToDiscard) {
            hand.size
        } else {
            numCardsToDiscard
        }

        addAction(DiscardCardsFromHand(numCards, "", optional))
    }

    override fun addCardFromDiscardToTopOfDeck(optional: Boolean, maxCost: Int?) {
        addAction(CardFromDiscardToTopOfDeck(optional, this.cardsInDiscardCopy, maxCost))
    }

    override fun addCardFromDiscardToHand() {
        addAction(CardFromDiscardToHand(this.cardsInDiscardCopy))
    }

    override fun addCardFromHandToTopOfDeck(cardFilter: ((Card) -> Boolean)?, chooseCardActionCard: ChooseCardActionCard?) {
        addAction(CardFromHandToTopOfDeck(cardFilter, chooseCardActionCard))
    }

    override fun waitForOtherPlayersToResolveActions() {
        if (currentAction !is WaitForOtherPlayersActions && actionsQueue.none { it is WaitForOtherPlayersActions }) {
            addAction(WaitForOtherPlayersActions(this))
        }
    }

    override fun waitForOtherPlayersForResolveAttack(attackCard: Card, info: Any?) {
        addAction(WaitForOtherPlayersForResolveAttack(this, attackCard, info))
    }

    override fun waitForOtherPlayersToResolveActionsWithResults(resultHandler: ActionResultHandler) {
        addAction(WaitForOtherPlayersActionsWithResults(this, resultHandler))
    }

    override fun chooseCardForOpponentToGain(cost: Int, text: String, destination: CardLocation, opponent: Player) {
        addAction(ChooseCardForOpponentToGain(cost, text, destination, opponent))
    }

    override fun chooseCardFromHand(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(ChooseCardFromHand(chooseCardActionCard, text, cardActionableExpression))
    }

    override fun chooseCardFromHandOptional(text: String, chooseCardActionCard: ChooseCardActionCardOptional, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(ChooseCardFromHandOptional(chooseCardActionCard, text, cardActionableExpression))
    }

    override fun chooseCardsFromHand(text: String, numToChoose: Int, optional: Boolean, chooseCardsActionCard: ChooseCardsActionCard, cardActionableExpression: ((card: Card) -> Boolean)?, info: Any?, allowDoNotUse: Boolean) {
        addAction(ChooseCardsFromHand(numToChoose, text, optional, chooseCardsActionCard, cardActionableExpression, info, allowDoNotUse))
    }

    override fun chooseCardFromSupply(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)?, info: Any?, choosingEmptyPilesAllowed: Boolean) {
        addAction(ChooseCardFromSupply(chooseCardActionCard, text, cardActionableExpression, info, choosingEmptyPilesAllowed))
    }

    override fun chooseCardAction(text: String, chooseCardActionCard: ChooseCardActionCard, cardsToSelectFrom: List<Card>, optional: Boolean, info: Any?) {
        addAction(ChooseCardAction(text, chooseCardActionCard, cardsToSelectFrom, optional, info))
    }

    override fun chooseCardsAction(numCardsToChoose: Int, text: String, chooseCardsActionCard: ChooseCardsActionCard, cardsToSelectFrom: List<Card>, optional: Boolean, info: Any?) {
        addAction(ChooseCardsAction(numCardsToChoose, text, chooseCardsActionCard, cardsToSelectFrom, optional, info))
    }

    fun showTavernCards() {
        addAction(ShowTavernCardsAction())
    }
}
