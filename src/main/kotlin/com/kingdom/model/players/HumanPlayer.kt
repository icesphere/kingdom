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
        addAction(DiscardCardsFromHandForBenefit(card, numCardsToDiscard, text, false))
    }

    override fun makeChoice(card: ChoiceActionCard, vararg choices: Choice) {
        addAction(ChoiceAction(card, *choices))
    }

    override fun makeChoice(card: ChoiceActionCard, text: String, vararg choices: Choice) {
        addAction(ChoiceAction(card, text, *choices))
    }

    override fun trashCardsFromHand(numCardsToTrash: Int, optional: Boolean) {
        if (optional) {
            addAction(TrashCardsFromHand(numCardsToTrash, "You may trash a card from your hand", true))
        } else {
            addAction(TrashCardsFromHand(numCardsToTrash, "", false))
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

    override fun passCardFromHandToPlayerOnLeft() {
        addAction(CardFromHandToPlayerOnLeft(game.getPlayerToLeft(this)))
    }

    override fun acquireFreeCard(maxCost: Int?) {
        addAction(FreeCardFromSupply(maxCost, "Acquire a free card from the supply costing up to $maxCost"))
    }

    override fun acquireFreeCardWithCost(cost: Int) {
        addAction(FreeCardFromSupply(null, "Acquire a free card from the supply costing $cost", exactCost = cost))
    }

    override fun acquireFreeCardForBenefit(maxCost: Int?, text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard) {
        addAction(FreeCardFromSupplyForBenefit(freeCardFromSupplyForBenefitActionCard, maxCost, text))
    }

    override fun acquireFreeCardToTopOfDeck(maxCost: Int?) {
        addAction(FreeCardFromSupply(maxCost, "Acquire a free card from the supply to the top of your deck costing up to $maxCost", CardLocation.Deck))
    }

    override fun acquireFreeCardToHand(maxCost: Int?) {
        addAction(FreeCardFromSupply(maxCost, "Acquire a free card from the supply to your hand costing up to $maxCost", CardLocation.Hand))
    }

    override fun acquireFreeCardOfTypeToHand(maxCost: Int?, cardType: CardType) {
        addAction(FreeCardFromSupply(maxCost, "Acquire a free card from the supply to your hand costing up to $maxCost", CardLocation.Hand, cardType))
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

    override fun discardCardsFromHand(numCardsToDiscard: Int, optional: Boolean) {
        addAction(DiscardCardsFromHand(numCardsToDiscard, "", optional))
    }

    override fun addCardAction(card: CardActionCard, text: String) {
        addAction(CardAction(card, text))
    }

    override fun addCardFromDiscardToTopOfDeck(maxCost: Int?) {
        addAction(CardFromDiscardToTopOfDeck(this.discard, maxCost))
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

    override fun chooseCardFromSupply(text: String, chooseCardActionCard: ChooseCardActionCard) {
        addAction(ChooseCardFromSupply(chooseCardActionCard, text))
    }

    override fun chooseCardAction(text: String, chooseCardActionCard: ChooseCardActionCard, cardsToSelectFrom: List<Card>, optional: Boolean, info: Any?) {
        addAction(ChooseCardAction(text, chooseCardActionCard, cardsToSelectFrom, optional, info))
    }
}
