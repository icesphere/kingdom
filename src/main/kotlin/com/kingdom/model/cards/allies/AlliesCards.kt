package com.kingdom.model.cards.allies

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.*
import com.kingdom.model.cards.listeners.*
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Barbarian : AlliesCard(NAME, CardType.ActionAttack, 5), AttackCard {
    init {
        addCoins = 2
        special = "Each other player trashes the top card of their deck. If it costs \$3 or more they gain a cheaper card sharing a type with it; otherwise they gain a Curse."
        isCurseGiver = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            val trashedCard = opponent.removeTopCardOfDeck()
            if (trashedCard == null) {
                opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor found no card to trash")
                return@forEach
            }

            opponent.cardTrashed(trashedCard, true)
            val trashedCost = opponent.getCardCostWithModifiers(trashedCard)
            if (trashedCost >= 3) {
                val availableCards = opponent.game.availableCards.filter {
                    it.debtCost == 0 && opponent.getCardCostWithModifiers(it) < trashedCost && it.sharesTypeWith(trashedCard)
                }
                if (availableCards.isNotEmpty()) {
                    opponent.chooseSupplyCardToGain(
                            { card -> card.debtCost == 0 && opponent.getCardCostWithModifiers(card) < trashedCost && card.sharesTypeWith(trashedCard) },
                            "Gain a cheaper card sharing a type with ${trashedCard.cardNameWithBackgroundColor}")
                }
            } else if (opponent.game.isCardAvailableInSupply(Curse())) {
                opponent.gainSupplyCard(Curse(), true)
            }
        }
    }

    companion object {
        const val NAME = "Barbarian"
    }
}

class Bauble : AlliesCard(NAME, CardType.Treasure, 2, "Liaison"), ChoiceActionCard {
    private val selectedChoices = mutableSetOf<Int>()

    init {
        special = "Choose two different options: +1 Buy; +\$1; +1 Favor; this turn, when you gain a card, you may put it onto your deck."
        isTreasureExcludedFromAutoPlay = true
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        selectedChoices.clear()
        chooseOption(player)
    }

    private fun chooseOption(player: Player) {
        val choices = listOf(
                Choice(1, "+1 Buy"),
                Choice(2, "+\$1"),
                Choice(3, "+1 Favor"),
                Choice(4, "Put gained card onto deck")
        ).filterNot { selectedChoices.contains(it.choiceNumber) }

        player.makeChoiceFromList(this, "Choose ${if (selectedChoices.isEmpty()) "two" else "one"} different option${if (selectedChoices.isEmpty()) "s" else ""}", choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        selectedChoices.add(choice)
        when (choice) {
            1 -> player.addBuys(1)
            2 -> player.addCoins(1)
            3 -> player.addFavors(1)
            4 -> player.numCardGainedMayPutOnTopOfDeck++
        }

        if (selectedChoices.size < 2) {
            chooseOption(player)
        }
    }

    companion object {
        const val NAME = "Bauble"
    }
}

class Broker : AlliesCard(NAME, CardType.Action, 4, "Liaison"), TrashCardsForBenefitActionCard, ChoiceActionCard {
    init {
        special = "Trash a card from your hand and choose one: +1 Card per \$1 it costs; or +1 Action per \$1 it costs; or +\$1 per \$1 it costs; or +1 Favor per \$1 it costs."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isEmpty()) return
        val amount = player.getCardCostWithModifiers(trashedCards.first())
        player.makeChoiceWithInfo(this, "Choose what to get $amount of", amount,
                Choice(1, "Cards"),
                Choice(2, "Actions"),
                Choice(3, "Coins"),
                Choice(4, "Favors"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val amount = info as Int
        when (choice) {
            1 -> player.drawCards(amount)
            2 -> player.addActions(amount)
            3 -> player.addCoins(amount)
            4 -> player.addFavors(amount)
        }
    }

    companion object {
        const val NAME = "Broker"
    }
}

class CapitalCity : AlliesCard(NAME, CardType.Action, 5), DiscardCardsForBenefitActionCard, ChoiceActionCard {
    init {
        addCards = 1
        addActions = 2
        special = "You may discard 2 cards for +\$2. You may pay \$2 for +2 Cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.yesNoChoice(this, "Discard 2 cards for +\$2?")
        } else {
            askPayForCards(player)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (info == "pay") {
            if (choice == 1 && player.availableCoins >= 2) {
                player.addCoins(-2)
                player.drawCards(2)
                player.addEventLogWithUsername("paid \$2 for +2 Cards")
            }
            return
        }

        if (choice == 1) {
            player.discardCardsForBenefit(this, 2, "Discard 2 cards for +\$2")
        } else {
            askPayForCards(player)
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        if (discardedCards.size == 2) {
            player.addCoins(2)
        }
        askPayForCards(player)
    }

    private fun askPayForCards(player: Player) {
        if (player.availableCoins >= 2) {
            player.yesNoChoice(this, "Pay \$2 for +2 Cards?", "pay")
        }
    }

    companion object {
        const val NAME = "Capital City"
    }
}

class Carpenter : AlliesCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {
    init {
        special = "If no Supply piles are empty, +1 Action and gain a card costing up to \$4. Otherwise, trash a card from your hand and gain a card costing up to \$2 more than it."
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.game.emptyPileNames.isEmpty()) {
            player.addActions(1)
            player.chooseSupplyCardToGainWithMaxCost(4)
        } else {
            player.trashCardsFromHandForBenefit(this, 1)
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isNotEmpty()) {
            player.chooseSupplyCardToGainWithMaxCost(player.getCardCostWithModifiers(trashedCards.first()) + 2)
        }
    }

    companion object {
        const val NAME = "Carpenter"
    }
}

class Contract : AlliesCard(NAME, CardType.TreasureDuration, 5, "Liaison"),
        ConditionalDuration, ChooseCardActionCard, ChoiceActionCard, SetAsideCardsDuration, StartOfTurnDurationAction {

    private var setAsideCard: Card? = null

    override val isKeepAtEndOfTurn: Boolean
        get() = setAsideCard != null

    override val setAsideCards: List<Card>?
        get() = setAsideCard?.let { listOf(it) } ?: emptyList()

    init {
        addFavors = 1
        special = "You may set aside an Action from your hand to play it at the start of your next turn."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        setAsideCard = null
        if (player.hand.any { it.isAction }) {
            player.yesNoChoice(this, "Set aside an Action from your hand to play it at the start of your next turn?", "setAside")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "setAside") {
            player.chooseCardFromHand("Set aside an Action from your hand to play it at the start of your next turn", this) { it.isAction }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardFromHand(card)
        setAsideCard = card
        player.cardsToPlayAtStartOfNextTurn.add(card)
        player.addEventLogWithUsername("set aside ${card.cardNameWithBackgroundColor} with $cardNameWithBackgroundColor")
    }

    override fun durationStartOfTurnAction(player: Player) {
        setAsideCard = null
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        setAsideCard = null
    }

    companion object {
        const val NAME = "Contract"
    }
}

class Courier : AlliesCard(NAME, CardType.Action, 4), ChooseCardActionCard {
    init {
        addCoins = 1
        special = "Discard the top card of your deck. Look through your discard pile; you may play an Action or Treasure from it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardTopCardOfDeck()
        val playableCards = player.cardsInDiscard.filter { it.isAction || it.isTreasure }
        if (playableCards.isNotEmpty()) {
            player.chooseCardAction("You may play an Action or Treasure from your discard pile", this, playableCards, true)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.playCardFromDiscard(card)
    }

    companion object {
        const val NAME = "Courier"
    }
}

class Emissary : AlliesCard(NAME, CardType.Action, 5, "Liaison") {
    init {
        special = "+3 Cards. If this made you shuffle at least one card, +1 Action and +2 Favors."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val shuffled = player.wouldShuffleToDraw(3)
        player.drawCards(3)
        if (shuffled) {
            player.addActions(1)
            player.addFavors(2)
        }
    }

    companion object {
        const val NAME = "Emissary"
    }
}

class Galleria : AlliesCard(NAME, CardType.Action, 5), AfterCardGainedListenerForCardsInPlay {
    init {
        addCoins = 3
        special = "This turn, when you gain a card costing \$3 or \$4, +1 Buy."
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (player.getCardCostWithModifiers(card) in 3..4) {
            player.addBuys(1)
        }
    }

    companion object {
        const val NAME = "Galleria"
    }
}

class Guildmaster : AlliesCard(NAME, CardType.Action, 5, "Liaison"), AfterCardGainedListenerForCardsInPlay {
    init {
        addCoins = 3
        special = "This turn, when you gain a card, +1 Favor."
    }

    override fun afterCardGained(card: Card, player: Player) {
        player.addFavors(1)
    }

    companion object {
        const val NAME = "Guildmaster"
    }
}

class Highwayman : AlliesCard(NAME, CardType.ActionAttackDuration, 5), StartOfTurnDurationAction {
    var isAttackActive: Boolean = false

    init {
        special = "At the start of your next turn, discard this from play and +3 Cards. Until then, the first Treasure each other player plays each turn does nothing."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        isAttackActive = true
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCards(3)
        isAttackActive = false
        player.removeDurationCardInPlay(this, CardLocation.Discard)
        player.addCardToDiscard(this)
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        isAttackActive = false
    }

    companion object {
        const val NAME = "Highwayman"
    }
}

class Hunter : AlliesCard(NAME, CardType.Action, 5) {
    init {
        addActions = 1
        special = "Reveal the top 3 cards of your deck. From those cards, put an Action, a Treasure, and a Victory card into your hand. Discard the rest."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val revealedCards = player.removeTopCardsOfDeck(3, revealCards = true).toMutableList()
        val cardsToHand = mutableListOf<Card>()

        listOf<(Card) -> Boolean>({ it.isAction }, { it.isTreasure }, { it.isVictory }).forEach { matcher ->
            val card = revealedCards.firstOrNull(matcher)
            if (card != null) {
                revealedCards.remove(card)
                cardsToHand.add(card)
            }
        }

        player.addCardsToHand(cardsToHand, true)
        player.addCardsToDiscard(revealedCards, true)
    }

    companion object {
        const val NAME = "Hunter"
    }
}

class Importer : AlliesCard(NAME, CardType.ActionDuration, 3, "Liaison"), StartOfTurnDurationAction, GameStartedListener {
    init {
        special = "At the start of your next turn, gain a card costing up to \$5. Setup: Each player gets +4 Favors."
        fontSize = 9
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.chooseSupplyCardToGainWithMaxCost(5)
    }

    override fun onGameStarted(game: Game) {
        game.players.forEach {
            it.addFavors(4)
            it.showInfoMessage("Setup for $cardNameWithBackgroundColor gave everyone +4 Favors")
        }
    }

    companion object {
        const val NAME = "Importer"
    }
}

class Innkeeper : AlliesCard(NAME, CardType.Action, 4), ChoiceActionCard {
    init {
        addActions = 1
        special = "Choose one: +1 Card; or +3 Cards, then discard 3 cards; or +5 Cards, then discard 6 cards."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "+1 Card"), Choice(2, "+3 Cards, discard 3"), Choice(3, "+5 Cards, discard 6"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> player.drawCard()
            2 -> {
                player.drawCards(3)
                player.discardCardsFromHand(3, false)
            }
            3 -> {
                player.drawCards(5)
                player.discardCardsFromHand(6, false)
            }
        }
    }

    companion object {
        const val NAME = "Innkeeper"
    }
}

class Marquis : AlliesCard(NAME, CardType.Action, 6) {
    init {
        addBuys = 1
        special = "+1 Card per card in your hand. Discard down to 10 cards in hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.drawCards(player.hand.size)
        if (player.hand.size > 10) {
            player.discardCardsFromHand(player.hand.size - 10, false)
        }
    }

    companion object {
        const val NAME = "Marquis"
    }
}

class MerchantCamp : AlliesCard(NAME, CardType.Action, 3), CardDiscardedFromPlayListener, ChoiceActionCard {
    init {
        addActions = 2
        addCoins = 1
        special = "When you discard this from play, you may put it onto your deck."
    }

    override fun onCardDiscarded(player: Player) {
        player.yesNoChoice(this, "Put ${cardNameWithBackgroundColor} onto your deck?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.removeCardFromDiscard(this)
            player.addCardToTopOfDeck(this)
        }
    }

    companion object {
        const val NAME = "Merchant Camp"
    }
}

class Modify : AlliesCard(NAME, CardType.Action, 5), TrashCardsForBenefitActionCard, ChoiceActionCard {
    private var trashedCard: Card? = null

    init {
        special = "Trash a card from your hand. Choose one: +1 Card and +1 Action; or gain a card costing up to \$2 more than the trashed card."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        trashedCard = null
        player.trashCardsFromHandForBenefit(this, 1)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isEmpty()) return
        trashedCard = trashedCards.first()
        player.makeChoice(this, Choice(1, "+1 Card and +1 Action"), Choice(2, "Gain a card"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = trashedCard ?: return
        if (choice == 1) {
            player.drawCard()
            player.addActions(1)
        } else {
            player.chooseSupplyCardToGainWithMaxCost(player.getCardCostWithModifiers(card) + 2)
        }
    }

    companion object {
        const val NAME = "Modify"
    }
}

class RoyalGalley : AlliesCard(NAME, CardType.ActionDuration, 4),
        ConditionalDuration, ChooseCardActionCard, ChoiceActionCard, StartOfTurnDurationAction, SetAsideCardsDuration {
    private var setAsideCard: Card? = null

    override val isKeepAtEndOfTurn: Boolean
        get() = setAsideCard != null

    override val setAsideCards: List<Card>?
        get() = setAsideCard?.let { listOf(it) } ?: emptyList()

    init {
        addCards = 1
        special = "You may play a non-Duration Action card from your hand. Set it aside; if you did, then at the start of your next turn, play it."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        setAsideCard = null
        if (player.hand.any { it.isAction && !it.isDuration }) {
            player.yesNoChoice(this, "Play a non-Duration Action card from your hand?", "play")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "play") {
            player.chooseCardFromHand("Play a non-Duration Action card from your hand", this) { it.isAction && !it.isDuration }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.playCard(card)
        if (player.inPlay.contains(card)) {
            player.removeCardInPlay(card, CardLocation.SetAside)
            setAsideCard = card
            player.cardsToPlayAtStartOfNextTurn.add(card)
            player.addEventLogWithUsername("set aside ${card.cardNameWithBackgroundColor} with $cardNameWithBackgroundColor")
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        setAsideCard = null
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        setAsideCard = null
    }

    companion object {
        const val NAME = "Royal Galley"
    }
}

class Sentinel : AlliesCard(NAME, CardType.Action, 3), ChoiceActionCard {
    private val cardsForAction = mutableListOf<Card>()
    private val cardsToPutBack = mutableListOf<Card>()
    private var numTrashed = 0

    init {
        special = "Look at the top 5 cards of your deck. You may trash up to 2 of them. Put the rest back in any order."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        cardsForAction.clear()
        cardsToPutBack.clear()
        numTrashed = 0
        cardsForAction.addAll(player.removeTopCardsOfDeck(5, revealCards = true))
        chooseNext(player)
    }

    private fun chooseNext(player: Player) {
        if (cardsForAction.isEmpty()) {
            if (cardsToPutBack.size == 1) {
                player.addCardToTopOfDeck(cardsToPutBack.first(), false)
            } else if (cardsToPutBack.isNotEmpty()) {
                player.putCardsOnTopOfDeckInAnyOrder(cardsToPutBack)
            }
            return
        }

        val choices = if (numTrashed < 2) listOf(Choice(1, "Trash"), Choice(2, "Put back")) else listOf(Choice(2, "Put back"))
        player.makeChoiceFromList(this, "Choose for ${cardsForAction.first().cardNameWithBackgroundColor}", choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = cardsForAction.removeAt(0)
        if (choice == 1) {
            numTrashed++
            player.cardTrashed(card, true)
        } else {
            cardsToPutBack.add(card)
        }
        chooseNext(player)
    }

    companion object {
        const val NAME = "Sentinel"
    }
}

class Skirmisher : AlliesCard(NAME, CardType.ActionAttack, 5), AttackCard, AfterCardGainedListenerForCardsInPlay {
    init {
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "This turn, when you gain an Attack card, each other player discards down to 3 cards in hand."
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.isAttack) {
            player.triggerAttack(this)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.filter { it.hand.size > 3 }.forEach {
            it.discardCardsFromHand(it.hand.size - 3, false)
        }
    }

    companion object {
        const val NAME = "Skirmisher"
    }
}

class Specialist : AlliesCard(NAME, CardType.Action, 5), ChooseCardActionCard, ChoiceActionCard {
    private var playedCard: Card? = null

    init {
        special = "You may play an Action or Treasure from your hand. Choose one: Play it again; or gain a copy of it."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        playedCard = null
        if (player.hand.any { it.isAction || it.isTreasure }) {
            player.yesNoChoice(this, "Play an Action or Treasure from your hand?", "play")
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        playedCard = card
        player.playCard(card)
        val choices = mutableListOf(Choice(1, "Play it again"))
        if (player.game.isCardAvailableInSupply(card)) {
            choices.add(Choice(2, "Gain a copy"))
        }
        player.makeChoiceFromList(this, "Choose one for ${card.cardNameWithBackgroundColor}", choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (info == "play") {
            if (choice == 1) {
                player.chooseCardFromHand("Play an Action or Treasure from your hand", this) { it.isAction || it.isTreasure }
            }
            return
        }

        val card = playedCard ?: return
        if (choice == 1) {
            player.addActions(1)
            player.playCard(card, repeatedAction = true)
        } else {
            player.gainSupplyCard(card, true)
        }
    }

    companion object {
        const val NAME = "Specialist"
    }
}

class Swap : AlliesCard(NAME, CardType.Action, 5), ChooseCardActionCard, ChoiceActionCard {
    init {
        addCards = 1
        addActions = 1
        special = "You may return an Action from your hand to its pile, to gain to your hand a different Action card costing up to \$5."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isAction }) {
            player.yesNoChoice(this, "Return an Action from your hand to its pile?", "return")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "return") {
            player.chooseCardFromHand("Return an Action from your hand to its pile", this) { it.isAction }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        if (info is Card) {
            player.gainSupplyCardToHand(card, true)
            return
        }

        player.removeCardFromHand(card)
        player.game.returnCardToSupply(card)
        val returnedCard = card
        player.chooseCardFromSupply("Gain a different Action card costing up to \$5 to your hand", this,
                { it.isAction && it.name != returnedCard.name && it.debtCost == 0 && player.getCardCostWithModifiers(it) <= 5 },
                returnedCard,
                choosingEmptyPilesAllowed = false)
    }

    companion object {
        const val NAME = "Swap"
    }
}

class Sycophant : AlliesCard(NAME, CardType.Action, 2, "Liaison"),
        DiscardCardsForBenefitActionCard, AfterCardGainedListenerForSelf, AfterCardTrashedListenerForSelf {
    init {
        addActions = 1
        special = "Discard 3 cards. If you discarded at least one, +\$3. When you gain or trash this, +2 Favors."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsForBenefit(this, 3, "Discard 3 cards")
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        if (discardedCards.isNotEmpty()) {
            player.addCoins(3)
        }
    }

    override fun afterCardGained(player: Player) {
        player.addFavors(2)
    }

    override fun afterCardTrashed(player: Player) {
        player.addFavors(2)
    }

    companion object {
        const val NAME = "Sycophant"
    }
}

class Town : AlliesCard(NAME, CardType.Action, 4), ChoiceActionCard {
    init {
        special = "Choose one: +1 Card and +2 Actions; or +1 Buy and +\$2."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "+1 Card and +2 Actions"), Choice(2, "+1 Buy and +\$2"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.drawCard()
            player.addActions(2)
        } else {
            player.addBuys(1)
            player.addCoins(2)
        }
    }

    companion object {
        const val NAME = "Town"
    }
}

class Underling : AlliesCard(NAME, CardType.Action, 3, "Liaison") {
    init {
        addCards = 1
        addActions = 1
        addFavors = 1
    }

    companion object {
        const val NAME = "Underling"
    }
}
