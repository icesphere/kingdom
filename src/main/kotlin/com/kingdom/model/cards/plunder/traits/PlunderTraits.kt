package com.kingdom.model.cards.plunder.traits

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Trait
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.cards.listeners.GameStartedListener
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.cards.plunder.UsesLoot
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

abstract class PlunderTrait(name: String) : Trait(name, Deck.Plunder)

private fun Trait.traitedTopCard(player: Player): Card? {
    return player.game.topCardForTrait(this)
}

private fun Trait.gainTraitedCard(player: Player) {
    val card = traitedTopCard(player) ?: return
    if (player.game.isCardAvailableInSupply(card)) {
        player.gainSupplyCard(card, true)
    }
}

class Cheap : PlunderTrait(NAME), CardCostModifier {

    init {
        setTraitRulesText("Cheap cards cost \$1 less.")
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        return if (appliesTo(card)) -1 else 0
    }

    companion object {
        const val NAME: String = "Cheap"
    }
}

class Cursed : PlunderTrait(NAME), UsesLoot {

    init {
        setTraitRulesText("When you gain a Cursed card, gain a Loot and a Curse.")
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (appliesTo(card)) {
            player.gainLoot(true)
            player.gainSupplyCard(Curse(), true)
        }
    }

    companion object {
        const val NAME: String = "Cursed"
    }
}

class Fated : PlunderTrait(NAME) {

    init {
        setTraitRulesText("When shuffling, you may look through the cards and reveal Fated cards to put them on the top or bottom.")
    }

    override fun afterShuffle(player: Player) {
        val fatedCards = player.deck.filter { appliesTo(it) }
        if (fatedCards.isEmpty()) {
            return
        }

        player.deck.removeAll(fatedCards)
        player.deck.addAll(0, fatedCards)
        player.addEventLogWithUsername("put ${fatedCards.size} Fated card(s) on top of their deck")
        player.refreshPlayerHandArea()
    }

    companion object {
        const val NAME: String = "Fated"
    }
}

class Fawning : PlunderTrait(NAME) {

    init {
        setTraitRulesText("When you gain a Province, gain a Fawning card.")
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.isProvince) {
            gainTraitedCard(player)
        }
    }

    companion object {
        const val NAME: String = "Fawning"
    }
}

class Friendly : PlunderTrait(NAME), ChooseCardActionCardOptional {

    init {
        setTraitRulesText("At the start of your Clean-up phase, you may discard a Friendly card to gain a Friendly card.")
    }

    override fun onStartOfCleanup(player: Player) {
        if (player.hand.any { appliesTo(it) } && traitedTopCard(player)?.let { player.game.isCardAvailableInSupply(it) } == true) {
            player.chooseCardFromHandOptional("You may discard a Friendly card to gain a Friendly card", this) { appliesTo(it) }
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card == null) {
            return
        }

        player.removeCardFromHand(card)
        player.discardCard(card, showLog = true)
        gainTraitedCard(player)
    }

    companion object {
        const val NAME: String = "Friendly"
    }
}

class Hasty : PlunderTrait(NAME) {

    init {
        setTraitRulesText("When you gain a Hasty card, set it aside, and play it at the start of your next turn.")
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        if (!appliesTo(card)) {
            return false
        }

        player.cardsToPlayAtStartOfNextTurn.add(card)
        player.cardRemovedFromPlay(card, CardLocation.SetAside)
        player.addEventLogWithUsername("set aside ${card.cardNameWithBackgroundColor} to play next turn")
        return true
    }

    companion object {
        const val NAME: String = "Hasty"
    }
}

class Inherited : PlunderTrait(NAME), GameStartedListener {

    init {
        setTraitRulesText("Setup: You start the game with an Inherited card in place of a starting card you choose.")
    }

    override fun onGameStarted(game: Game) {
        val inheritedCard = game.topCardForTrait(this) ?: return
        game.players.forEach { player ->
            if (game.isCardAvailableInSupply(inheritedCard)) {
                game.removeCardFromSupply(inheritedCard, false)
                player.replaceOneStartingCardWith(game.getNewInstanceOfCard(inheritedCard.name))
            }
        }
        game.refreshSupply()
    }

    companion object {
        const val NAME: String = "Inherited"
    }
}

class Inspiring : PlunderTrait(NAME), ChooseCardActionCardOptional {

    init {
        setTraitRulesText("After playing an Inspiring card on your turn, you may play an Action from your hand that you don't have a copy of in play.")
    }

    override fun afterCardPlayed(card: Card, player: Player) {
        if (appliesTo(card) && player.hand.any { it.isAction && player.inPlayWithDuration.none { inPlayCard -> inPlayCard.name == it.name } }) {
            player.chooseCardFromHandOptional("You may play an Action from your hand that you don't have a copy of in play", this) {
                it.isAction && player.inPlayWithDuration.none { inPlayCard -> inPlayCard.name == it.name }
            }
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card != null) {
            player.addActions(1)
            player.playCard(card)
        }
    }

    companion object {
        const val NAME: String = "Inspiring"
    }
}

class Nearby : PlunderTrait(NAME) {

    init {
        setTraitRulesText("When you gain a Nearby card, +1 Buy.")
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (appliesTo(card)) {
            player.addBuys(1)
        }
    }

    companion object {
        const val NAME: String = "Nearby"
    }
}

class Patient : PlunderTrait(NAME), ChooseCardsActionCard {

    init {
        setTraitRulesText("At the start of your Clean-up phase, you may set aside Patient cards from your hand to play them at the start of your next turn.")
    }

    override fun onStartOfCleanup(player: Player) {
        val patientCards = player.hand.filter { appliesTo(it) }
        if (patientCards.isNotEmpty()) {
            player.chooseCardsFromHand("Set aside any Patient cards from your hand to play next turn", patientCards.size, true, this, { appliesTo(it) })
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        player.removeCardsFromHand(cards)
        player.cardsToPlayAtStartOfNextTurn.addAll(cards)
    }

    companion object {
        const val NAME: String = "Patient"
    }
}

class Pious : PlunderTrait(NAME) {

    init {
        setTraitRulesText("When you gain a Pious card, you may trash a card from your hand.")
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (appliesTo(card) && player.hand.isNotEmpty()) {
            player.trashCardFromHand(true)
        }
    }

    companion object {
        const val NAME: String = "Pious"
    }
}

class Reckless : PlunderTrait(NAME) {

    init {
        setTraitRulesText("Follow the instructions of played Reckless cards twice. When discarding one from play, return it to its pile.")
    }

    override fun afterCardPlayed(card: Card, player: Player) {
        if (!appliesTo(card) || player.isRecklessRepeatInProgress) {
            return
        }

        player.isRecklessRepeatInProgress = true
        try {
            if (card.isAction) {
                player.addActions(1)
            }
            player.playCard(card, repeatedAction = true, showLog = false)
        } finally {
            player.isRecklessRepeatInProgress = false
        }
    }

    override fun handleDiscardFromPlay(card: Card, player: Player): Boolean {
        if (!appliesTo(card)) {
            return false
        }

        player.cardRemovedFromPlay(card, CardLocation.Supply)
        player.game.returnCardToSupply(card)
        player.addEventLogWithUsername("returned ${card.cardNameWithBackgroundColor} to its pile")
        return true
    }

    companion object {
        const val NAME: String = "Reckless"
    }
}

class Rich : PlunderTrait(NAME) {

    init {
        setTraitRulesText("When you gain a Rich card, gain a Silver.")
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (appliesTo(card)) {
            player.gainSupplyCard(Silver(), true)
        }
    }

    companion object {
        const val NAME: String = "Rich"
    }
}

class Shy : PlunderTrait(NAME), ChooseCardActionCardOptional {

    init {
        setTraitRulesText("At the start of your turn, you may discard one Shy card for +2 Cards.")
    }

    override fun onStartOfTurn(player: Player) {
        if (player.hand.any { appliesTo(it) }) {
            player.chooseCardFromHandOptional("You may discard one Shy card for +2 Cards", this) { appliesTo(it) }
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card != null) {
            player.removeCardFromHand(card)
            player.discardCard(card, showLog = true)
            player.drawCards(2)
        }
    }

    companion object {
        const val NAME: String = "Shy"
    }
}

class Tireless : PlunderTrait(NAME) {

    init {
        setTraitRulesText("When you discard a Tireless card from play, set it aside, and put it onto your deck at end of turn.")
    }

    override fun handleDiscardFromPlay(card: Card, player: Player): Boolean {
        if (!appliesTo(card)) {
            return false
        }

        player.cardRemovedFromPlay(card, CardLocation.SetAside)
        player.cardsToPutOnTopOfDeckAfterDrawingCardsAtEndOfTurn.add(card)
        player.addEventLogWithUsername("set aside ${card.cardNameWithBackgroundColor} to put onto their deck at end of turn")
        return true
    }

    companion object {
        const val NAME: String = "Tireless"
    }
}
