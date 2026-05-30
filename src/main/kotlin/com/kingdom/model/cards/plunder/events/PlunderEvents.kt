package com.kingdom.model.cards.plunder.events

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterShuffleListener
import com.kingdom.model.cards.listeners.BeforeShuffleListenerForEventsBought
import com.kingdom.model.cards.listeners.CardGainedListenerForEventsBought
import com.kingdom.model.cards.plunder.UsesLoot
import com.kingdom.model.cards.supply.Duchy
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.players.Player

private fun Player.playCardFromHandForFree(card: Card) {
    addActions(1)
    playCard(card)
}

private fun Player.playGainedCardForFree(card: Card) {
    inPlay.add(card)
    addActions(1)
    playCard(card, repeatedAction = true)
    game.refreshCardsPlayed()
}

class Avoid : PlunderEvent(NAME, 2), BeforeShuffleListenerForEventsBought, AfterShuffleListener {

    private var shufflesRemaining = 0
    private val cardsKeptFromShuffle = mutableListOf<Card>()

    init {
        special = "+1 Buy. The next time you shuffle this turn, keep up to 3 of those cards in your discard pile."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addBuys(1)
        shufflesRemaining++
    }

    override fun beforeShuffle(player: Player) {
        if (shufflesRemaining <= 0 || player.cardsInDiscard.isEmpty()) {
            return
        }

        val cardsToKeep = player.cardsInDiscard.take(3)
        player.removeCardsFromDiscard(cardsToKeep)
        cardsKeptFromShuffle.addAll(cardsToKeep)
        shufflesRemaining--
    }

    override fun afterShuffle(player: Player) {
        if (cardsKeptFromShuffle.isEmpty()) {
            return
        }

        player.addCardsToDiscard(cardsKeptFromShuffle, true)
        cardsKeptFromShuffle.clear()
    }

    companion object {
        const val NAME: String = "Avoid"
    }
}

class Bury : PlunderEvent(NAME, 1), ChooseCardActionCard {

    init {
        special = "+1 Buy. Put any card from your discard pile on the bottom of your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addBuys(1)
        if (player.cardsInDiscard.isNotEmpty()) {
            player.chooseCardAction("Choose a card from your discard pile to put on the bottom of your deck", this, player.cardsInDiscardCopy, true)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardFromDiscard(card)
        player.addCardToBottomOfDeck(card)
    }

    companion object {
        const val NAME: String = "Bury"
    }
}

class Deliver : PlunderEvent(NAME, 2), CardGainedListenerForEventsBought {

    init {
        special = "+1 Buy. This turn, each time you gain a card, set it aside, and put it into your hand at end of turn."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addBuys(1)
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        player.cardsToPutIntoHandAfterDrawingCardsAtEndOfTurn.add(card)
        player.cardRemovedFromPlay(card, CardLocation.SetAside)
        player.addEventLogWithUsername("set aside ${card.cardNameWithBackgroundColor} for ${cardNameWithBackgroundColor}")
        return true
    }

    companion object {
        const val NAME: String = "Deliver"
    }
}

class Foray : PlunderEvent(NAME, 3), DiscardCardsForBenefitActionCard, UsesLoot {

    init {
        special = "Discard 3 cards, revealing them. If they have 3 different names, gain a Loot."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.hand.size >= 3
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsForBenefit(this, 3, special)
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        if (discardedCards.distinctBy { it.name }.size == 3) {
            player.gainLoot(true)
        }
    }

    companion object {
        const val NAME: String = "Foray"
    }
}

class Invasion : PlunderEvent(NAME, 10), ChooseCardActionCardOptional, UsesLoot {

    init {
        special = "You may play an Attack from your hand. Gain a Duchy. Gain an Action onto your deck. Gain a Loot; play it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isAttack }) {
            player.chooseCardFromHandOptional("You may play an Attack from your hand", this) { it.isAttack }
        } else {
            gainCards(player)
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card != null) {
            player.playCardFromHandForFree(card)
        }
        gainCards(player)
    }

    private fun gainCards(player: Player) {
        player.gainSupplyCard(Duchy(), true)
        if (player.game.availableCards.none { it.isAction }) {
            gainAndPlayLoot(player)
            return
        }
        player.chooseCardFromSupply("Gain an Action onto your deck", object : ChooseCardActionCard {
            override fun onCardChosen(player: Player, card: Card, info: Any?) {
                player.gainSupplyCard(card, true, CardLocation.Deck)
                gainAndPlayLoot(player)
            }
        }, { it.isAction }, "gainAction", false)
    }

    private fun gainAndPlayLoot(player: Player) {
        val loot = player.gainLoot(true, CardLocation.Hand) ?: return
        if (player.hand.contains(loot)) {
            player.playCard(loot)
        }
    }

    companion object {
        const val NAME: String = "Invasion"
    }
}

class Journey : PlunderEvent(NAME, 4, true) {

    init {
        special = "Once per turn: If the previous turn wasn't yours, you don't discard cards from play in Clean-up this turn, and take an extra turn after this one."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.game.previousPlayerId != player.userId
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.keepCardsInPlayAtCleanup = true
        player.game.isExtraTurnForCurrentPlayer = true
    }

    companion object {
        const val NAME: String = "Journey"
    }
}

class Launch : PlunderEvent(NAME, 3, true) {

    init {
        special = "Once per turn: Return to your Action phase. +1 Card, +1 Action, and +1 Buy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.returnToActionPhaseIfBuyPhase()
        player.drawCard()
        player.addActions(1)
        player.addBuys(1)
    }

    companion object {
        const val NAME: String = "Launch"
    }
}

class Looting : PlunderEvent(NAME, 6), UsesLoot {

    init {
        special = "Gain a Loot."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainLoot(true)
    }

    companion object {
        const val NAME: String = "Looting"
    }
}

class Maelstrom : PlunderEvent(NAME, 4), TrashCardsForBenefitActionCard {

    init {
        special = "Trash 3 cards from your hand. Each other player with 5 or more cards in hand trashes one of them."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.hand.size >= 3
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 3, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        player.opponents.filter { it.hand.size >= 5 }
                .forEach { it.trashCardFromHand(false) }
    }

    companion object {
        const val NAME: String = "Maelstrom"
    }
}

class Mirror : PlunderEvent(NAME, 3), CardGainedListenerForEventsBought {

    private var actionGainsRemaining = 0

    init {
        special = "+1 Buy. The next time you gain an Action card this turn, gain a copy of it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addBuys(1)
        actionGainsRemaining++
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        if (actionGainsRemaining > 0 && card.isAction && player.game.isCardAvailableInSupply(card)) {
            actionGainsRemaining--
            player.gainSupplyCard(card, true)
        }
        return false
    }

    companion object {
        const val NAME: String = "Mirror"
    }
}

class Peril : PlunderEvent(NAME, 2), TrashCardsForBenefitActionCard, UsesLoot {

    init {
        special = "You may trash an Action card from your hand to gain a Loot."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.hand.any { it.isAction }
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHandForBenefit(this, 1, special, { it.isAction })
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isNotEmpty()) {
            player.gainLoot(true)
        }
    }

    companion object {
        const val NAME: String = "Peril"
    }
}

class Prepare : PlunderEvent(NAME, 3) {

    init {
        special = "Set aside your hand face up. At the start of your next turn, play those Actions and Treasures in any order, then discard the rest."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.hand.toList()
        player.removeCardsFromHand(cards)
        player.cardsToPlayAtStartOfNextTurn.addAll(cards.filter { it.isAction || it.isTreasure })
        player.cardsToDiscardAtStartOfNextTurn.addAll(cards.filterNot { it.isAction || it.isTreasure })
        player.addEventLogWithUsername("set aside their hand for ${cardNameWithBackgroundColor}")
    }

    companion object {
        const val NAME: String = "Prepare"
    }
}

class Prosper : PlunderEvent(NAME, 10), UsesLoot, ChooseCardsActionCard {

    init {
        special = "Gain a Loot, plus any number of differently named Treasures."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainLoot(true)
        val treasures = player.game.availableCards
                .filter { it.isTreasure }
                .distinctBy { it.name }
        if (treasures.isNotEmpty()) {
            player.chooseCardsAction(treasures.size, "Gain any number of differently named Treasures", this, treasures, true)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        cards.distinctBy { it.name }.forEach { player.gainSupplyCard(it, true) }
    }

    companion object {
        const val NAME: String = "Prosper"
    }
}

class Rush : PlunderEvent(NAME, 2), CardGainedListenerForEventsBought {

    private var actionGainsRemaining = 0

    init {
        special = "+1 Buy. The next time you gain an Action card this turn, play it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addBuys(1)
        actionGainsRemaining++
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        if (actionGainsRemaining > 0 && card.isAction) {
            actionGainsRemaining--
            player.playGainedCardForFree(card)
            return true
        }
        return false
    }

    companion object {
        const val NAME: String = "Rush"
    }
}

class Scrounge : PlunderEvent(NAME, 3), ChoiceActionCard {

    init {
        special = "Choose one: Trash a card from your hand; or gain an Estate from the trash, and if you did, gain a card costing up to \$5."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && (player.hand.isNotEmpty() || player.game.trashedCards.any { it.isEstate })
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val choices = mutableListOf<Choice>()
        if (player.hand.isNotEmpty()) {
            choices.add(Choice(1, "Trash a card from your hand"))
        }
        if (player.game.trashedCards.any { it.isEstate }) {
            choices.add(Choice(2, "Gain an Estate from the trash"))
        }
        player.makeChoiceFromList(this, special, choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardFromHand(false)
            return
        }

        val estate = player.game.trashedCards.firstOrNull { it.isEstate } ?: return
        player.game.trashedCards.remove(estate)
        player.cardGained(estate)
        player.addEventLogWithUsername("gained ${Estate().cardNameWithBackgroundColor} from the trash")
        player.chooseSupplyCardToGainWithMaxCost(5)
    }

    companion object {
        const val NAME: String = "Scrounge"
    }
}
