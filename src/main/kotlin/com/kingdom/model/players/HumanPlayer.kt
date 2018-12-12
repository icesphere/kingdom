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

    private fun addAction(action: Action) {
        actionsQueue.add(action)

        if (currentAction == null) {
            resolveActions()
        }
    }

    override fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(TrashCardsFromHand(numCardsToTrash, text, true, cardActionableExpression))
    }

    override fun optionallyTrashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(TrashCardsFromHandForBenefit(card, numCardsToTrash, text, true, cardActionableExpression))
    }

    override fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(TrashCardsFromHandForBenefit(card, numCardsToTrash, text, false, cardActionableExpression))
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

    override fun chooseSupplyCardToGain(maxCost: Int?, cardActionableExpression: ((card: Card) -> Boolean)?, text: String?) {
        val actionText = when {
            text != null -> text
            maxCost != null -> "Gain a free card from the supply costing up to $maxCost"
            else -> "Gain a free card from the supply"
        }
        addAction(FreeCardFromSupply(maxCost, actionText, cardActionableExpression))
    }

    override fun chooseSupplyCardToGainWithExactCost(cost: Int) {
        addAction(FreeCardFromSupply(null, "Gain a free card from the supply costing $cost", {c -> getCardCostWithModifiers(c) == cost}))
    }

    override fun chooseSupplyCardToGainForBenefit(maxCost: Int?, text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard) {
        addAction(FreeCardFromSupplyForBenefit(freeCardFromSupplyForBenefitActionCard, maxCost, text, null))
    }

    override fun chooseSupplyCardToGainToTopOfDeck(maxCost: Int?) {
        addAction(FreeCardFromSupply(maxCost, "Gain a free card from the supply to the top of your deck costing up to $maxCost", null, CardLocation.Deck))
    }

    override fun chooseSupplyCardToGainToTopOfDeckWithExactCost(cost: Int) {
        addAction(FreeCardFromSupply(null, "Gain a free card from the supply to the top of your deck costing $cost", {c -> getCardCostWithModifiers(c) == cost}, CardLocation.Deck))
    }

    override fun chooseSupplyCardToGainToHandWithMaxCost(maxCost: Int?) {
        addAction(FreeCardFromSupply(maxCost, "Gain a free card from the supply to your hand costing up to $maxCost", null, CardLocation.Hand))
    }

    override fun chooseSupplyCardToGainToHandWithMaxCostAndType(maxCost: Int?, cardType: CardType) {
        addAction(FreeCardFromSupply(maxCost, "Gain a free card from the supply to your hand costing up to $maxCost", { c -> c.type == cardType }, CardLocation.Hand))
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

    override fun addCardAction(card: CardActionCard, text: String) {
        addAction(CardAction(card, text))
    }

    override fun addCardFromDiscardToTopOfDeck(maxCost: Int?) {
        addAction(CardFromDiscardToTopOfDeck(this.cardsInDiscardCopy, maxCost))
    }

    override fun addCardFromHandToTopOfDeck(cardFilter: ((Card) -> Boolean)?, chooseCardActionCard: ChooseCardActionCard?) {
        addAction(CardFromHandToTopOfDeck(cardFilter, chooseCardActionCard))
    }

    override fun waitForOtherPlayersToResolveActions() {
        addAction(WaitForOtherPlayersActions(this))
    }

    override fun waitForOtherPlayersForResolveAttack(attackCard: Card) {
        addAction(WaitForOtherPlayersForResolveAttack(this, attackCard))
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

    override fun chooseCardsFromHand(text: String, numToChoose: Int, optional: Boolean, chooseCardsActionCard: ChooseCardsActionCard, cardActionableExpression: ((card: Card) -> Boolean)?) {
        addAction(ChooseCardsFromHand(numToChoose, text, optional, chooseCardsActionCard, cardActionableExpression))
    }

    override fun chooseCardFromSupply(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)?, info: Any?) {
        addAction(ChooseCardFromSupply(chooseCardActionCard, text, cardActionableExpression, info))
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
