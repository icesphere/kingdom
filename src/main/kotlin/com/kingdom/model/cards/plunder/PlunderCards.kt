package com.kingdom.model.cards.plunder

import com.kingdom.model.Choice
import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.*
import com.kingdom.model.cards.listeners.*
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

private const val COMMAND = "Command"

private fun Card.isCommand(): Boolean = additionalTypes.contains(COMMAND)

private fun Player.drawUntilHandSize(size: Int) {
    if (hand.size < size) {
        drawCards(size - hand.size)
    }
}

private fun Player.playCardFromHandForFree(card: Card) {
    addActions(1)
    playCard(card)
}

private fun Player.playCardFromDiscardForFree(card: Card) {
    removeCardFromDiscard(card)
    addCardToHand(card)
    playCardFromHandForFree(card)
}

private fun Player.trashCardFromAnyPlayArea(card: Card) {
    when {
        inPlay.contains(card) -> trashCardInPlay(card)
        durationCards.contains(card) -> {
            addEventLogWithUsername("trashed ${card.cardNameWithBackgroundColor} from play")
            removeDurationCardInPlay(card, CardLocation.Trash)
            cardTrashed(card)
        }
        else -> {
            removeCard(card)
            cardTrashed(card, true)
        }
    }
}

class Abundance : PlunderCard(NAME, CardType.TreasureDuration, 4), AfterCardGainedListenerForCardsInPlay,
        ConditionalDuration, MultipleTurnDuration {

    private var waitingForActionGain = false

    override val isKeepAtEndOfTurn: Boolean
        get() = waitingForActionGain

    init {
        special = "The next time you gain an Action card, +1 Buy and +\$3."
        fontSize = 12
    }

    override fun cardPlayedSpecialAction(player: Player) {
        waitingForActionGain = true
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (waitingForActionGain && card.isAction) {
            waitingForActionGain = false
            player.addBuys(1)
            player.addCoins(3)
            player.showInfoMessage("${cardNameWithBackgroundColor} gave you +1 Buy and +\$3")
        }
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = waitingForActionGain

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        waitingForActionGain = false
    }

    companion object {
        const val NAME = "Abundance"
    }
}

class BuriedTreasure : PlunderCard(NAME, CardType.TreasureDuration, 5), StartOfTurnDurationAction,
        AfterCardGainedListenerForSelf {

    init {
        special = "At the start of your next turn, +1 Buy and +\$3. When you gain this, play it."
        fontSize = 10
        isTreasureExcludedFromAutoPlay = true
    }

    override fun afterCardGained(player: Player) {
        player.removeCard(this)
        player.addCardToHand(this)
        player.playCard(this)
        player.addEventLogWithUsername("played ${cardNameWithBackgroundColor} when they gained it")
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addBuys(1)
        player.addCoins(3)
    }

    companion object {
        const val NAME = "Buried Treasure"
    }
}

class CabinBoy : PlunderCard(NAME, CardType.ActionDuration, 4), StartOfTurnDurationAction, ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        special = "At the start of your next turn, either +\$2, or trash this to gain a Duration card."
        fontSize = 10
    }

    override fun durationStartOfTurnAction(player: Player) {
        val choices = mutableListOf(Choice(1, "+\$2"))
        if (player.game.availableCards.any { it.isDuration }) {
            choices.add(Choice(2, "Trash this to gain a Duration"))
        }
        player.makeChoiceFromList(this, choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addCoins(2)
        } else {
            player.trashCardFromAnyPlayArea(this)
            player.chooseSupplyCardToGain({ it.isDuration }, "Gain a Duration card")
        }
    }

    companion object {
        const val NAME = "Cabin Boy"
    }
}

class Cage : PlunderCard(NAME, CardType.TreasureDuration, 2), ChooseCardsActionCard, SetAsideCardsDuration,
        AfterCardGainedListenerForCardsInPlay, ConditionalDuration, MultipleTurnDuration {

    override var setAsideCards: List<Card>? = emptyList()
    private var waitingForVictoryGain = false

    override val isKeepAtEndOfTurn: Boolean
        get() = waitingForVictoryGain || setAsideCards?.isNotEmpty() == true

    init {
        special = "Set aside up to 4 cards from your hand. The next time you gain a Victory card, trash this and put the set aside cards into your hand."
        fontSize = 9
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        waitingForVictoryGain = true
        player.chooseCardsFromHand("Set aside up to 4 cards from your hand", 4, true, this)
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        setAsideCards = cards.toList()
        player.removeCardsFromHand(cards)
        if (cards.isNotEmpty()) {
            player.addEventLogWithUsername("set aside ${cards.size} card(s) with ${cardNameWithBackgroundColor}")
        }
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (waitingForVictoryGain && card.isVictory) {
            waitingForVictoryGain = false
            val cards = setAsideCards ?: emptyList()
            if (cards.isNotEmpty()) {
                player.cardsToPutIntoHandAfterDrawingCardsAtEndOfTurn.addAll(cards)
                player.showInfoMessage("$cardNameWithBackgroundColor will put ${cards.size} set aside card(s) into your hand at end of turn")
            }
            setAsideCards = emptyList()
            player.trashCardFromAnyPlayArea(this)
        }
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = isKeepAtEndOfTurn

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        waitingForVictoryGain = false
        setAsideCards = emptyList()
    }

    companion object {
        const val NAME = "Cage"
    }
}

class Crew : PlunderCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction {

    init {
        addCards = 3
        special = "At the start of your next turn, put this onto your deck."
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.removeDurationCardInPlay(this, CardLocation.Deck)
        player.addCardToTopOfDeck(this)
    }

    companion object {
        const val NAME = "Crew"
    }
}

class Crucible : PlunderCard(NAME, CardType.Treasure, 4), TrashCardsForBenefitActionCard {

    init {
        special = "Trash a card from your hand, for +\$1 per \$1 it costs."
        fontSize = 11
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        trashedCards.firstOrNull()?.let {
            player.addCoins(player.getCardCostWithModifiers(it))
        }
    }

    companion object {
        const val NAME = "Crucible"
    }
}

class Cutthroat : PlunderCard(NAME, CardType.ActionAttackDuration, 5), AttackCard,
        AfterCardGainedListenerForCardsInPlay, AfterOtherPlayerCardGainedListenerForCardsInPlay,
        ConditionalDuration, MultipleTurnDuration, UsesLoot {

    private var waitingForTreasureGain = false

    override val isKeepAtEndOfTurn: Boolean
        get() = waitingForTreasureGain

    init {
        special = "Each other player discards down to 3 cards in hand. The next time anyone gains a Treasure costing \$5 or more, gain a Loot."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        waitingForTreasureGain = true
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.filter { it.hand.size > 3 }
                .forEach { it.discardCardsFromHand(it.hand.size - 3, false) }
    }

    override fun afterCardGained(card: Card, player: Player) {
        maybeGainLoot(card, player)
    }

    override fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        maybeGainLoot(card, player)
    }

    private fun maybeGainLoot(card: Card, player: Player) {
        if (waitingForTreasureGain && card.isTreasure && player.getCardCostWithModifiers(card) >= 5) {
            waitingForTreasureGain = false
            player.gainLoot()
        }
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = waitingForTreasureGain

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        waitingForTreasureGain = false
    }

    companion object {
        const val NAME = "Cutthroat"
    }
}

class Enlarge : PlunderCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction,
        TrashCardsForBenefitActionCard {

    init {
        special = "Trash a card from your hand. Gain a card costing up to \$2 more than it. At the start of your next turn, do this again."
        isTrashingCard = true
        isTrashingFromHandToUpgradeCard = true
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        trashAndGain(player)
    }

    override fun durationStartOfTurnAction(player: Player) {
        trashAndGain(player)
    }

    private fun trashAndGain(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand to gain a card costing up to \$2 more")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val trashedCard = trashedCards.firstOrNull() ?: return
        val maxCost = player.getCardCostWithModifiers(trashedCard) + 2
        player.chooseSupplyCardToGainWithMaxCost(maxCost)
    }

    companion object {
        const val NAME = "Enlarge"
    }
}

class Figurine : PlunderCard(NAME, CardType.Treasure, 5), ChooseCardActionCardOptional {

    init {
        addCards = 2
        special = "You may discard an Action card for +1 Buy and +\$1."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isAction }) {
            player.chooseCardFromHandOptional("Discard an Action card for +1 Buy and +\$1?", this) { it.isAction }
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card != null) {
            player.discardCardFromHand(card)
            player.addBuys(1)
            player.addCoins(1)
        }
    }

    companion object {
        const val NAME = "Figurine"
    }
}

class FirstMate : PlunderCard(NAME, CardType.Action, 5), ChooseCardActionCardOptional {

    init {
        special = "Play any number of Action cards with the same name from your hand. Then draw until you have 6 cards in hand."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isAction }) {
            player.chooseCardFromHandOptional("Choose an Action card to play all copies of from your hand", this) { it.isAction }
        } else {
            player.drawUntilHandSize(6)
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card != null) {
            player.hand.filter { it.name == card.name && it.isAction }.toList().forEach {
                player.playCardFromHandForFree(it)
            }
        }
        player.drawUntilHandSize(6)
    }

    companion object {
        const val NAME = "First Mate"
    }
}

class Flagship : PlunderCard(NAME, CardType.ActionDuration, 4, COMMAND), CardPlayedListenerForCardsInPlay,
        ConditionalDuration, MultipleTurnDuration {

    private var waitingForAction = false

    override val isKeepAtEndOfTurn: Boolean
        get() = waitingForAction

    init {
        addCoins = 2
        special = "The next time you play a non-Command Action card, play it again afterwards."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        waitingForAction = true
    }

    override fun onCardPlayed(card: Card, player: Player) {
        if (waitingForAction && card != this && card.isAction && !card.isCommand()) {
            waitingForAction = false
            player.addRepeatCardAction(card)
            player.addEventLogWithUsername("${cardNameWithBackgroundColor} will replay ${card.cardNameWithBackgroundColor}")
        }
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = waitingForAction

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        waitingForAction = false
    }

    companion object {
        const val NAME = "Flagship"
    }
}

class FortuneHunter : PlunderCard(NAME, CardType.Action, 4) {

    init {
        addCoins = 2
        special = "Look at the top 3 cards of your deck. You may play a Treasure from them. Put the rest back in any order."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val revealed = player.removeTopCardsOfDeck(3, revealCards = true).toMutableList()
        val treasure = revealed.firstOrNull { it.isTreasure }
        if (treasure != null) {
            revealed.remove(treasure)
            player.addCardToHand(treasure)
            player.playCard(treasure)
        }
        player.addCardsToTopOfDeck(revealed)
    }

    companion object {
        const val NAME = "Fortune Hunter"
    }
}

class Frigate : PlunderCard(NAME, CardType.ActionAttackDuration, 5), AttackCard, StartOfTurnDurationAction,
        AfterOtherPlayerCardPlayedListenerForCardsInPlay {

    private var attackActive = false

    init {
        addCoins = 3
        special = "Until your next turn, when another player plays an Action card, they discard down to 4 cards in hand."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        attackActive = true
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
    }

    override fun durationStartOfTurnAction(player: Player) {
        attackActive = false
    }

    override fun afterCardPlayedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        if (attackActive && card.isAction && !playersExcludedFromCardEffects.contains(otherPlayer) && otherPlayer.hand.size > 4) {
            otherPlayer.discardCardsFromHand(otherPlayer.hand.size - 4, false)
            otherPlayer.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor made you discard down to 4 cards")
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        attackActive = false
    }

    companion object {
        const val NAME = "Frigate"
    }
}

class Gondola : PlunderCard(NAME, CardType.TreasureDuration, 4), ChoiceActionCard, ChooseCardActionCard,
        ConditionalDuration, StartOfTurnDurationAction, AfterCardGainedListenerForSelf {

    private var coinsNextTurn = false

    override val isKeepAtEndOfTurn: Boolean
        get() = coinsNextTurn

    init {
        special = "Choose one: +\$2 now, or at the start of your next turn, +\$2. When you gain this, you may play an Action card from your hand."
        fontSize = 9
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoiceFromList(this, "Choose one", listOf(Choice(1, "+\$2 now"), Choice(2, "+\$2 next turn")))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (info == "gain" && choice == 1) {
            player.chooseCardFromHand("Play an Action card from your hand", this) { it.isAction }
            return
        }

        if (choice == 1) {
            player.addCoins(2)
        } else {
            coinsNextTurn = true
        }
    }

    override fun afterCardGained(player: Player) {
        if (player.hand.any { it.isAction }) {
            player.yesNoChoice(this, "Play an Action card from your hand?", "gain")
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.playCardFromHandForFree(card)
    }

    override fun durationStartOfTurnAction(player: Player) {
        if (coinsNextTurn) {
            player.addCoins(2)
            coinsNextTurn = false
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        coinsNextTurn = false
    }

    companion object {
        const val NAME = "Gondola"
    }
}

class Grotto : PlunderCard(NAME, CardType.ActionDuration, 2), ChooseCardsActionCard, SetAsideCardsDuration,
        ConditionalDuration, StartOfTurnDurationAction {

    override var setAsideCards: List<Card>? = emptyList()

    override val isKeepAtEndOfTurn: Boolean
        get() = setAsideCards?.isNotEmpty() == true

    init {
        addActions = 1
        special = "Set aside any number of cards from your hand. At the start of your next turn, discard them and +1 Card per card discarded."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardsFromHand("Set aside up to 4 cards from your hand", 4.coerceAtMost(player.hand.size), true, this)
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        setAsideCards = cards.toList()
        player.removeCardsFromHand(cards)
    }

    override fun durationStartOfTurnAction(player: Player) {
        val cards = setAsideCards ?: emptyList()
        if (cards.isNotEmpty()) {
            player.addCardsToDiscard(cards, true)
            player.drawCards(cards.size)
        }
        setAsideCards = emptyList()
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        setAsideCards = emptyList()
    }

    companion object {
        const val NAME = "Grotto"
    }
}

class HarborVillage : PlunderCard(NAME, CardType.Action, 4), CardPlayedListenerForCardsInPlay {

    private var watchingNextCard = false

    init {
        addCards = 1
        addActions = 2
        special = "The next time you play a card this turn, if it gives you +\$, +\$1."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        watchingNextCard = true
    }

    override fun onCardPlayed(card: Card, player: Player) {
        if (watchingNextCard && card != this && card.isAction) {
            watchingNextCard = false
            if (card.addCoins > 0 || card.isAddCoinsCard) {
                player.addCoins(1)
            }
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        watchingNextCard = false
    }

    companion object {
        const val NAME = "Harbor Village"
    }
}

class JewelledEgg : PlunderCard(NAME, CardType.Treasure, 2), AfterCardTrashedListenerForSelf, UsesLoot {

    init {
        addCoins = 1
        addBuys = 1
        special = "When you trash this, gain a Loot."
        fontSize = 12
    }

    override fun afterCardTrashed(player: Player) {
        player.gainLoot()
    }

    companion object {
        const val NAME = "Jewelled Egg"
    }
}

class KingsCache : PlunderCard(NAME, CardType.Treasure, 7), ChooseCardActionCard {

    init {
        special = "You may play a Treasure from your hand three times."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isTreasure }) {
            player.chooseCardFromHand("Play a Treasure from your hand three times", this) { it.isTreasure }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.playCard(card)
        repeat(2) {
            player.playCard(card, repeatedAction = true)
        }
    }

    companion object {
        const val NAME = "King's Cache"
    }
}

class LandingParty : PlunderCard(NAME, CardType.ActionDuration, 4), CardPlayedListenerForCardsInPlay,
        ConditionalDuration, MultipleTurnDuration {

    private var waitingForFirstTreasure = false

    override val isKeepAtEndOfTurn: Boolean
        get() = waitingForFirstTreasure

    init {
        addCards = 2
        addActions = 2
        special = "The next time the first card you play on a turn is a Treasure, put this onto your deck."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        waitingForFirstTreasure = true
    }

    override fun onCardPlayed(card: Card, player: Player) {
        if (waitingForFirstTreasure && card.isTreasure && player.cardsPlayed.size == 1 && player.durationCards.contains(this)) {
            waitingForFirstTreasure = false
            player.removeDurationCardInPlay(this, CardLocation.Deck)
            player.addCardToTopOfDeck(this)
        }
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = waitingForFirstTreasure

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        waitingForFirstTreasure = false
    }

    companion object {
        const val NAME = "Landing Party"
    }
}

class Longship : PlunderCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction {

    init {
        addActions = 2
        special = "At the start of your next turn, +2 Cards."
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCards(2)
    }

    companion object {
        const val NAME = "Longship"
    }
}

class Mapmaker : PlunderCard(NAME, CardType.ActionReaction, 4), AfterCardGainedListenerForCardsInHand,
        AfterOtherPlayerCardGainedListenerForCardsInHand, ChoiceActionCard {

    init {
        special = "Look at the top 4 cards of your deck. Put 2 into your hand and discard the rest. When any player gains a Victory card, you may play this from your hand."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(4, revealCards = true)
        player.addCardsToHand(cards.take(2), true)
        player.addCardsToDiscard(cards.drop(2), true)
    }

    override fun afterCardGained(card: Card, player: Player) {
        maybePlay(card, player)
    }

    override fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        maybePlay(card, player)
    }

    private fun maybePlay(card: Card, player: Player) {
        if (card.isVictory && player.hand.contains(this)) {
            player.yesNoChoice(this, "Play ${cardNameWithBackgroundColor} from your hand?", "play")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "play" && player.hand.contains(this)) {
            player.playCardFromHandForFree(this)
        }
    }

    companion object {
        const val NAME = "Mapmaker"
    }
}

class Maroon : PlunderCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {

    init {
        special = "Trash a card from your hand. +2 Cards per type it has."
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        trashedCards.firstOrNull()?.let {
            player.drawCards(it.numTypes * 2)
        }
    }

    companion object {
        const val NAME = "Maroon"
    }
}

class MiningRoad : PlunderCard(NAME, CardType.Action, 5), AfterCardGainedListenerForCardsInPlay {

    private var mayPlayTreasureGain = false

    init {
        addActions = 1
        addBuys = 1
        addCoins = 2
        special = "Once this turn, when you gain a Treasure, you may play it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        mayPlayTreasureGain = true
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (mayPlayTreasureGain && card.isTreasure) {
            mayPlayTreasureGain = false
            player.removeCard(card)
            player.addCardToHand(card)
            player.playCard(card)
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        mayPlayTreasureGain = false
    }

    companion object {
        const val NAME = "Mining Road"
    }
}

class Pendant : PlunderCard(NAME, CardType.Treasure, 5) {

    init {
        special = "+\$1 per differently named Treasure you have in play."
        fontSize = 12
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCoins(player.inPlayWithDuration.filter { it.isTreasure }.distinctBy { it.name }.size)
    }

    companion object {
        const val NAME = "Pendant"
    }
}

class Pickaxe : PlunderCard(NAME, CardType.Treasure, 5), TrashCardsForBenefitActionCard, UsesLoot {

    init {
        addCoins = 1
        special = "Trash a card from your hand. If it costs \$3 or more, gain a Loot to your hand."
        fontSize = 10
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        trashedCards.firstOrNull()?.let {
            if (player.getCardCostWithModifiers(it) >= 3) {
                player.gainLoot(destination = CardLocation.Hand)
            }
        }
    }

    companion object {
        const val NAME = "Pickaxe"
    }
}

class Pilgrim : PlunderCard(NAME, CardType.Action, 5), ChooseCardActionCard {

    init {
        addCards = 4
        special = "Put a card from your hand onto your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.chooseCardFromHand("Put a card from your hand onto your deck", this)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardFromHand(card)
        player.addCardToTopOfDeck(card)
    }

    companion object {
        const val NAME = "Pilgrim"
    }
}

class Quartermaster : PlunderCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction,
        MultipleTurnDuration, ChoiceActionCard, ChooseCardActionCard, SetAsideCardsDuration {

    private val cardsOnThis = mutableListOf<Card>()

    override val setAsideCards: List<Card>?
        get() = cardsOnThis

    init {
        special = "At the start of each of your turns for the rest of the game, either gain a card costing up to \$4, setting it aside here, or put a card from here into your hand."
        fontSize = 8
    }

    override fun durationStartOfTurnAction(player: Player) {
        val choices = mutableListOf<Choice>()
        if (player.game.availableCards.any { it.debtCost == 0 && player.getCardCostWithModifiers(it) <= 4 }) {
            choices.add(Choice(1, "Gain a card costing up to \$4"))
        }
        if (cardsOnThis.isNotEmpty()) {
            choices.add(Choice(2, "Put a card from here into hand"))
        }

        when (choices.size) {
            0 -> Unit
            1 -> actionChoiceMade(player, choices.first().choiceNumber, null)
            else -> player.makeChoiceFromList(this, choices)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.chooseCardFromSupply("Gain a card costing up to \$4, setting it aside here", this,
                    { it.debtCost == 0 && player.getCardCostWithModifiers(it) <= 4 }, "gain",
                    choosingEmptyPilesAllowed = false)
        } else if (cardsOnThis.isNotEmpty()) {
            val card = cardsOnThis.removeAt(0)
            player.addCardToHand(card)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        if (info == "gain") {
            player.nextCardGainedSetAsideAction = { gainedCard ->
                cardsOnThis.add(gainedCard)
                player.addEventLogWithUsername("set aside ${gainedCard.cardNameWithBackgroundColor} on ${cardNameWithBackgroundColor}")
            }
            player.gainSupplyCard(card, true, CardLocation.SetAside)
        }
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = true

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        if (cardsOnThis.isNotEmpty()) {
            player.addCardsToDiscard(cardsOnThis, true)
            cardsOnThis.clear()
        }
    }

    companion object {
        const val NAME = "Quartermaster"
    }
}

class Rope : PlunderCard(NAME, CardType.TreasureDuration, 4), StartOfTurnDurationAction {

    init {
        addBuys = 1
        addCoins = 1
        special = "At the start of your next turn, +1 Card, then you may trash a card from your hand."
        fontSize = 10
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCards(1)
        player.optionallyTrashCardsFromHand(1, "You may trash a card from your hand")
    }

    companion object {
        const val NAME = "Rope"
    }
}

class SackOfLoot : PlunderCard(NAME, CardType.Treasure, 6), UsesLoot {

    init {
        addBuys = 1
        addCoins = 1
        special = "Gain a Loot."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainLoot()
    }

    companion object {
        const val NAME = "Sack of Loot"
    }
}

class Search : PlunderCard(NAME, CardType.ActionDuration, 2), AfterCardGainedListenerForCardsInPlay,
        AfterOtherPlayerCardGainedListenerForCardsInPlay, ConditionalDuration, MultipleTurnDuration, UsesLoot {

    private var waitingForEmptyPile = false

    override val isKeepAtEndOfTurn: Boolean
        get() = waitingForEmptyPile

    init {
        addCoins = 2
        special = "The next time a Supply pile empties, trash this and gain a Loot."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        waitingForEmptyPile = true
    }

    override fun afterCardGained(card: Card, player: Player) {
        maybeGainLoot(card, player)
    }

    override fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        maybeGainLoot(card, player)
    }

    private fun maybeGainLoot(card: Card, player: Player) {
        if (waitingForEmptyPile && card.pileName in player.game.emptyPileNames) {
            waitingForEmptyPile = false
            player.trashCardFromAnyPlayArea(this)
            player.gainLoot()
        }
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = waitingForEmptyPile

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        waitingForEmptyPile = false
    }

    companion object {
        const val NAME = "Search"
    }
}

class SecludedShrine : PlunderCard(NAME, CardType.ActionDuration, 3), AfterCardGainedListenerForCardsInPlay,
        ChooseCardsActionCard, ConditionalDuration, MultipleTurnDuration {

    private var waitingForTreasureGain = false

    override val isKeepAtEndOfTurn: Boolean
        get() = waitingForTreasureGain

    init {
        addCoins = 1
        special = "The next time you gain a Treasure, you may trash up to 2 cards from your hand."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        waitingForTreasureGain = true
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (waitingForTreasureGain && card.isTreasure) {
            waitingForTreasureGain = false
            player.chooseCardsFromHand("Trash up to 2 cards from your hand", 2, true, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        cards.forEach { player.trashCardFromHand(it) }
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = waitingForTreasureGain

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        waitingForTreasureGain = false
    }

    companion object {
        const val NAME = "Secluded Shrine"
    }
}

class Shaman : PlunderCard(NAME, CardType.Action, 2), TurnStartedListenerForCardsInSupply {

    init {
        addActions = 1
        addCoins = 1
        special = "You may trash a card from your hand. At the start of your turn, gain a card from the trash costing up to \$6."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHand(1, "You may trash a card from your hand")
    }

    override fun turnStarted(player: Player) {
        if (player.game.trashedCards.any { player.getCardCostWithModifiers(it) <= 6 }) {
            player.gainCardFromTrash(false) { player.getCardCostWithModifiers(it) <= 6 }
        }
    }

    companion object {
        const val NAME = "Shaman"
    }
}

class SilverMine : PlunderCard(NAME, CardType.Treasure, 5) {

    init {
        addCoins = 2
        special = "Gain a Treasure to your hand costing less than this."
        fontSize = 12
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cost = player.getCardCostWithModifiers(this)
        player.chooseSupplyCardToGain({ it.isTreasure && it.debtCost == 0 && player.getCardCostWithModifiers(it) < cost },
                "Gain a Treasure to your hand costing less than ${cardNameWithBackgroundColor}", CardLocation.Hand)
    }

    companion object {
        const val NAME = "Silver Mine"
    }
}

class Siren : PlunderCard(NAME, CardType.ActionAttackDuration, 3), AttackCard, StartOfTurnDurationAction,
        AfterCardGainedListenerForSelf, ChooseCardActionCard {

    init {
        special = "Each other player gains a Curse. At the start of your next turn, draw until you have 8 cards in hand. When you gain this, trash it unless you trash an Action card from your hand."
        isCurseGiver = true
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach {
            if (it.game.isCardAvailableInSupply(Curse())) {
                it.gainSupplyCard(Curse(), true)
            }
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawUntilHandSize(8)
    }

    override fun afterCardGained(player: Player) {
        if (player.hand.any { it.isAction }) {
            player.chooseCardFromHand("Trash an Action card from your hand, or ${cardNameWithBackgroundColor} will be trashed", this) { it.isAction }
        } else {
            player.removeCard(this)
            player.cardTrashed(this, true)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.trashCardFromHand(card)
    }

    companion object {
        const val NAME = "Siren"
    }
}

class Stowaway : PlunderCard(NAME, CardType.ActionDurationReaction, 3), StartOfTurnDurationAction,
        AfterCardGainedListenerForCardsInHand, AfterOtherPlayerCardGainedListenerForCardsInHand, ChoiceActionCard {

    init {
        special = "At the start of your next turn, +2 Cards. When anyone gains a Duration card, you may play this from your hand."
        fontSize = 10
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCards(2)
    }

    override fun afterCardGained(card: Card, player: Player) {
        maybePlay(card, player)
    }

    override fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player) {
        maybePlay(card, player)
    }

    private fun maybePlay(card: Card, player: Player) {
        if (card.isDuration && player.hand.contains(this)) {
            player.yesNoChoice(this, "Play ${cardNameWithBackgroundColor} from your hand?", "play")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "play" && player.hand.contains(this)) {
            player.removeCardFromHand(this)
            player.durationCards.add(this)
            player.cardRemovedFromPlay(this, CardLocation.PlayArea)
            player.addEventLogWithUsername("played ${cardNameWithBackgroundColor} from hand")
        }
    }

    companion object {
        const val NAME = "Stowaway"
    }
}

class SwampShacks : PlunderCard(NAME, CardType.Action, 4) {

    init {
        addActions = 2
        special = "+1 Card per 3 cards you have in play, rounded down."
        fontSize = 12
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.drawCards(player.inPlay.size / 3)
    }

    companion object {
        const val NAME = "Swamp Shacks"
    }
}

class Taskmaster : PlunderCard(NAME, CardType.ActionDuration, 3), AfterCardGainedListenerForCardsInPlay,
        ConditionalDuration, MultipleTurnDuration, StartOfTurnDurationAction {

    private var watchingForFiveCostGain = false
    private var repeatNextTurn = false

    override val isKeepAtEndOfTurn: Boolean
        get() = repeatNextTurn

    init {
        addActions = 1
        addCoins = 1
        special = "The next time you gain a card costing exactly \$5 this turn, at the start of your next turn, repeat this ability."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        watchingForFiveCostGain = true
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (watchingForFiveCostGain && player.getCardCostWithModifiers(card) == 5) {
            watchingForFiveCostGain = false
            repeatNextTurn = true
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        repeatNextTurn = false
        watchingForFiveCostGain = true
        player.addActions(1)
        player.addCoins(1)
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = repeatNextTurn

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        watchingForFiveCostGain = false
        repeatNextTurn = false
    }

    companion object {
        const val NAME = "Taskmaster"
    }
}

class Tools : PlunderCard(NAME, CardType.Treasure, 4), ChoiceActionCard {

    init {
        special = "Gain a copy of a card anyone has in play."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.game.players.flatMap { it.inPlayWithDuration }
                .distinctBy { it.name }
                .filter { player.game.isCardAvailableInSupply(it) }
        if (cards.isNotEmpty()) {
            player.makeChoiceFromListWithInfo(this, "Gain a copy of a card anyone has in play", cards,
                    cards.mapIndexed { index, card -> Choice(index + 1, card.name) })
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val cards = info as? List<Card> ?: return
        cards.getOrNull(choice - 1)?.let { player.gainSupplyCard(it, true) }
    }

    companion object {
        const val NAME = "Tools"
    }
}

class Trickster : PlunderCard(NAME, CardType.ActionAttack, 5), AttackCard, StartOfCleanupListener,
        ChoiceActionCard {

    private var canSetAsideTreasure = false

    init {
        addCoins = 2
        special = "Each other player gains a Curse. Once this turn, when you discard a Treasure from play, you may set it aside and put it into your hand at end of turn."
        isCurseGiver = true
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        canSetAsideTreasure = true
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach {
            if (it.game.isCardAvailableInSupply(Curse())) {
                it.gainSupplyCard(Curse(), true)
            }
        }
    }

    override fun onStartOfCleanup(player: Player) {
        val treasure = player.inPlay.firstOrNull { it.isTreasure } ?: return
        if (canSetAsideTreasure) {
            player.yesNoChoice(this, "Set aside ${treasure.cardNameWithBackgroundColor} and put it into your hand at end of turn?", treasure)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = info as? Card ?: return
        if (choice == 1 && player.inPlay.contains(card)) {
            canSetAsideTreasure = false
            player.removeCardInPlay(card, CardLocation.SetAside)
            player.cardsToPutIntoHandAfterDrawingCardsAtEndOfTurn.add(card)
            player.addEventLogWithUsername("set aside ${card.cardNameWithBackgroundColor} with ${cardNameWithBackgroundColor}")
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        canSetAsideTreasure = false
    }

    companion object {
        const val NAME = "Trickster"
    }
}

class WealthyVillage : PlunderCard(NAME, CardType.Action, 5), AfterCardGainedListenerForSelf, UsesLoot {

    init {
        addCards = 1
        addActions = 2
        special = "When you gain this, if you have 3 or more differently named Treasures in play, gain a Loot."
        fontSize = 10
    }

    override fun afterCardGained(player: Player) {
        if (player.inPlayWithDuration.filter { it.isTreasure }.distinctBy { it.name }.size >= 3) {
            player.gainLoot()
        }
    }

    companion object {
        const val NAME = "Wealthy Village"
    }
}

class Amphora : LootCard(NAME, CardType.TreasureDuration), ChoiceActionCard, StartOfTurnDurationAction,
        ConditionalDuration {

    private var nextTurnBonus = false

    override val isKeepAtEndOfTurn: Boolean
        get() = nextTurnBonus

    init {
        special = "Choose one: +\$3 and +1 Buy now, or at the start of your next turn."
        fontSize = 12
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoiceFromList(this, "Choose one", listOf(Choice(1, "+\$3 and +1 Buy now"), Choice(2, "Next turn")))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addCoins(3)
            player.addBuys(1)
        } else {
            nextTurnBonus = true
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        if (nextTurnBonus) {
            player.addCoins(3)
            player.addBuys(1)
            nextTurnBonus = false
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        nextTurnBonus = false
    }

    companion object {
        const val NAME = "Amphora"
    }
}

class Doubloons : LootCard(NAME), AfterCardGainedListenerForSelf {

    init {
        addCoins = 3
        special = "When you gain this, gain a Gold."
    }

    override fun afterCardGained(player: Player) {
        if (player.game.isCardAvailableInSupply(Gold())) {
            player.gainSupplyCard(Gold(), true)
        }
    }

    companion object {
        const val NAME = "Doubloons"
    }
}

class EndlessChalice : LootCard(NAME, CardType.TreasureDuration), StartOfTurnDurationAction,
        MultipleTurnDuration {

    init {
        addCoins = 1
        addBuys = 1
        special = "At the start of each of your turns for the rest of the game, +1 Buy and +\$1."
        fontSize = 11
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addBuys(1)
        player.addCoins(1)
    }

    override fun keepAtEndOfTurn(player: Player): Boolean = true

    companion object {
        const val NAME = "Endless Chalice"
    }
}

class Figurehead : LootCard(NAME, CardType.TreasureDuration), StartOfTurnDurationAction {

    init {
        addCoins = 3
        special = "At the start of your next turn, +2 Cards."
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCards(2)
    }

    companion object {
        const val NAME = "Figurehead"
    }
}

class Hammer : LootCard(NAME), ChooseCardActionCard {

    init {
        addCoins = 3
        special = "Gain a card costing up to \$4."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Gain a card costing up to \$4", this,
                { it.debtCost == 0 && player.getCardCostWithModifiers(it) <= 4 },
                choosingEmptyPilesAllowed = false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.gainSupplyCard(card, true)
    }

    companion object {
        const val NAME = "Hammer"
    }
}

class Insignia : LootCard(NAME), AfterCardGainedListenerForCardsInPlay {

    private var topDeckGainedCards = false

    init {
        addCoins = 3
        special = "This turn, when you gain a card, you may put it onto your deck."
        fontSize = 12
    }

    override fun cardPlayedSpecialAction(player: Player) {
        topDeckGainedCards = true
        player.numCardGainedMayPutOnTopOfDeck++
    }

    override fun afterCardGained(card: Card, player: Player) {
    }

    override fun removedFromPlay(player: Player) {
        if (topDeckGainedCards) {
            player.numCardGainedMayPutOnTopOfDeck--
            topDeckGainedCards = false
        }
        super.removedFromPlay(player)
    }

    companion object {
        const val NAME = "Insignia"
    }
}

class Jewels : LootCard(NAME, CardType.TreasureDuration), StartOfTurnDurationAction {

    init {
        addCoins = 3
        addBuys = 1
        special = "At the start of your next turn, put this on the bottom of your deck."
        fontSize = 12
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.removeDurationCardInPlay(this, CardLocation.Deck)
        player.addCardToBottomOfDeck(this)
    }

    companion object {
        const val NAME = "Jewels"
    }
}

class Orb : LootCard(NAME), ChoiceActionCard {

    init {
        special = "Choose one: Play an Action or Treasure from your discard pile, or +1 Buy and +\$3."
        fontSize = 11
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val choices = mutableListOf(Choice(1, "+1 Buy and +\$3"))
        if (player.cardsInDiscard.any { it.isAction || it.isTreasure }) {
            choices.add(Choice(2, "Play an Action or Treasure from discard"))
        }
        player.makeChoiceFromList(this, choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 2) {
            player.cardsInDiscard.firstOrNull { it.isAction || it.isTreasure }?.let {
                player.playCardFromDiscardForFree(it)
            }
        } else {
            player.addBuys(1)
            player.addCoins(3)
        }
    }

    companion object {
        const val NAME = "Orb"
    }
}

class PrizeGoat : LootCard(NAME) {

    init {
        addCoins = 3
        addBuys = 1
        special = "You may trash a card from your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHand(1, "You may trash a card from your hand")
    }

    companion object {
        const val NAME = "Prize Goat"
    }
}

class PuzzleBox : LootCard(NAME), ChooseCardActionCardOptional {

    init {
        addCoins = 3
        addBuys = 1
        special = "Set aside a card from your hand face down. Put it into your hand at end of turn after drawing."
        fontSize = 11
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromHandOptional("Set aside a card from your hand to put into your hand at end of turn", this)
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card != null) {
            player.removeCardFromHand(card)
            player.cardsToPutIntoHandAfterDrawingCardsAtEndOfTurn.add(card)
        }
    }

    companion object {
        const val NAME = "Puzzle Box"
    }
}

class Sextant : LootCard(NAME) {

    init {
        addCoins = 3
        addBuys = 1
        special = "Look at the top 5 cards of your deck. Discard any number and put the rest back in any order."
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(5, revealCards = true)
        player.addCardsToTopOfDeck(cards)
    }

    companion object {
        const val NAME = "Sextant"
    }
}

class Shield : LootCard(NAME, CardType.TreasureReaction), HandBeforeAttackListener, ChoiceActionCard {

    private lateinit var attackCard: Card

    init {
        addCoins = 3
        addBuys = 1
        special = "When another player plays an Attack card, you may reveal this from your hand to be unaffected by it."
        isDefense = true
        fontSize = 10
    }

    override fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player) {
        this.attackCard = attackCard
        player.yesNoChoice(this, "Reveal $cardNameWithBackgroundColor to be unaffected by ${attackCard.cardNameWithBackgroundColor}?", attacker)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val attacker = info as Player
            attacker.showInfoMessage("${player.username} revealed $cardNameWithBackgroundColor to be unaffected by the attack")
            player.addEventLogWithUsername("revealed $cardNameWithBackgroundColor to be unaffected by ${attackCard.cardNameWithBackgroundColor}")
            attackCard.playersExcludedFromCardEffects.add(player)
        }
    }

    companion object {
        const val NAME = "Shield"
    }
}

class SpellScroll : LootCard(NAME, CardType.ActionTreasure), ChooseCardActionCard, ChoiceActionCard {

    init {
        special = "Trash this. Gain a cheaper card. If it's an Action or Treasure, you may play it."
        fontSize = 11
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardFromAnyPlayArea(this)
        player.chooseCardFromSupply("Gain a cheaper card", this,
                { it.debtCost == 0 && player.getCardCostWithModifiers(it) < player.getCardCostWithModifiers(this) },
                "gain", choosingEmptyPilesAllowed = false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.gainSupplyCard(card, true, CardLocation.Hand)
        val gainedCard = player.hand.lastOrNull { it.name == card.name } ?: return
        if (gainedCard.isAction || gainedCard.isTreasure) {
            player.yesNoChoice(this, "Play ${gainedCard.cardNameWithBackgroundColor}?", gainedCard)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = info as? Card ?: return
        if (choice == 1 && player.hand.contains(card)) {
            player.playCardFromHandForFree(card)
        }
    }

    companion object {
        const val NAME = "Spell Scroll"
    }
}

class Staff : LootCard(NAME), ChooseCardActionCardOptional {

    init {
        addCoins = 3
        addBuys = 1
        special = "You may play an Action card from your hand."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isAction }) {
            player.chooseCardFromHandOptional("Play an Action card from your hand", this) { it.isAction }
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        if (card != null) {
            player.playCardFromHandForFree(card)
        }
    }

    companion object {
        const val NAME = "Staff"
    }
}

class Sword : LootCard(NAME, CardType.TreasureAttack), AttackCard {

    init {
        addCoins = 3
        addBuys = 1
        special = "Each other player discards down to 4 cards in hand."
        fontSize = 12
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.filter { it.hand.size > 4 }
                .forEach { it.discardCardsFromHand(it.hand.size - 4, false) }
    }

    companion object {
        const val NAME = "Sword"
    }
}
