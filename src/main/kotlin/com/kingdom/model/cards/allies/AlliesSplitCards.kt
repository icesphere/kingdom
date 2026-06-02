package com.kingdom.model.cards.allies

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.*
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForCardsInPlay
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Augurs : AlliesSplitPile(NAME, 3) {
    override val otherCardsInPile: List<Card>
        get() = listOf(HerbGatherer(), Acolyte(), Sorceress(), Sibyl())

    override fun createMultiTypePile(game: Game): List<Card> = listOf(
            HerbGatherer(), HerbGatherer(), HerbGatherer(), HerbGatherer(),
            Acolyte(), Acolyte(), Acolyte(), Acolyte(),
            Sorceress(), Sorceress(), Sorceress(), Sorceress(),
            Sibyl(), Sibyl(), Sibyl(), Sibyl())

    companion object {
        const val NAME = "Augurs"
    }
}

class HerbGatherer : AlliesSplitCard(NAME, CardType.Action, 3, Augurs.NAME, "Augur"), ChooseCardActionCard, ChoiceActionCard {
    init {
        addBuys = 1
        special = "Put your deck into your discard pile. Look through it and you may play a Treasure from it. You may rotate the Augurs."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.putDeckIntoDiscard()
        val treasures = player.cardsInDiscard.filter { it.isTreasure }
        if (treasures.isNotEmpty()) {
            player.chooseCardAction("You may play a Treasure from your discard pile", this, treasures, true)
        }
        player.yesNoChoice(this, "Rotate the Augurs?", "rotate")
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.playCardFromDiscard(card)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "rotate") {
            player.rotatePile(Augurs.NAME)
        }
    }

    companion object {
        const val NAME = "Herb Gatherer"
    }
}

class Acolyte : AlliesSplitCard(NAME, CardType.Action, 4, Augurs.NAME, "Augur"),
        TrashCardsForBenefitActionCard, ChoiceActionCard {
    init {
        special = "You may trash an Action or Victory card from your hand to gain a Gold. You may trash this to gain an Augur."
        isTrashingCard = true
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isAction || it.isVictory }) {
            player.optionallyTrashCardsFromHandForBenefit(this, 1, "Trash an Action or Victory card to gain a Gold",
                    cardActionableExpression = { it.isAction || it.isVictory })
        }
        player.yesNoChoice(this, "Trash ${cardNameWithBackgroundColor} to gain an Augur?", "trashSelf")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isNotEmpty()) {
            player.gainSupplyCard(Gold(), true)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "trashSelf") {
            player.removeCardInPlay(this, CardLocation.Trash)
            player.cardTrashed(this, true)
            player.gainTopCardFromPile(Augurs.NAME)
        }
    }

    companion object {
        const val NAME = "Acolyte"
    }
}

class Sorceress : AlliesSplitCard(NAME, CardType.ActionAttack, 5, Augurs.NAME, "Augur"), ChooseCardActionCard, AttackCard {
    private var namedCard: Card? = null

    init {
        addActions = 1
        special = "Name a card. Reveal the top card of your deck and put it into your hand. If it’s the named card, each other player gains a Curse."
        fontSize = 9
        isCurseGiver = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardAction(special, this, player.game.allCardsCopy, false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        namedCard = card
        player.addEventLogWithUsername("named ${card.cardNameWithBackgroundColor}")
        val revealed = player.revealTopCardOfDeck()
        if (revealed != null) {
            player.removeTopCardOfDeck()
            player.addCardToHand(revealed, true)
            if (revealed.name == card.name) {
                player.triggerAttack(this)
            }
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.filter { it.game.isCardAvailableInSupply(Curse()) }.forEach {
            it.gainSupplyCard(Curse(), true)
        }
    }

    companion object {
        const val NAME = "Sorceress"
    }
}

class Sibyl : AlliesSplitCard(NAME, CardType.Action, 6, Augurs.NAME, "Augur"), ChooseCardActionCard {
    private var choosingBottom = false

    init {
        addCards = 4
        addActions = 1
        special = "Put a card from your hand on top of your deck, and another on the bottom."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        choosingBottom = false
        if (player.hand.isNotEmpty()) {
            player.chooseCardFromHand("Put a card from your hand on top of your deck", this)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.removeCardFromHand(card)
        if (!choosingBottom) {
            player.addCardToTopOfDeck(card, false)
            choosingBottom = true
            if (player.hand.isNotEmpty()) {
                player.chooseCardFromHand("Put a card from your hand on the bottom of your deck", this)
            }
        } else {
            player.addCardToBottomOfDeck(card, false)
            choosingBottom = false
        }
    }

    companion object {
        const val NAME = "Sibyl"
    }
}

class Clashes : AlliesSplitPile(NAME, 3) {
    override val otherCardsInPile: List<Card>
        get() = listOf(BattlePlan(), Archer(), Warlord(), Territory())

    override fun createMultiTypePile(game: Game): List<Card> = listOf(
            BattlePlan(), BattlePlan(), BattlePlan(), BattlePlan(),
            Archer(), Archer(), Archer(), Archer(),
            Warlord(), Warlord(), Warlord(), Warlord(),
            Territory(), Territory(), Territory(), Territory())

    companion object {
        const val NAME = "Clashes"
    }
}

class BattlePlan : AlliesSplitCard(NAME, CardType.Action, 3, Clashes.NAME, "Clash"), ChoiceActionCard, ChooseCardActionCard {
    init {
        addCards = 1
        addActions = 1
        special = "You may reveal an Attack card from your hand for +1 Card. You may rotate any Supply pile."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isAttack }) {
            player.yesNoChoice(this, "Reveal an Attack card from your hand for +1 Card?", "reveal")
        }
        player.chooseCardFromSupply("Choose a Supply pile to rotate", this, null, "rotate")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "reveal") {
            player.chooseCardFromHand("Reveal an Attack card from your hand", this) { it.isAttack }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        if (info == "rotate") {
            player.rotatePile(card.pileName)
        } else {
            player.revealCardFromHand(card)
            player.drawCard()
        }
    }

    companion object {
        const val NAME = "Battle Plan"
    }
}

class Archer : AlliesSplitCard(NAME, CardType.ActionAttack, 4, Clashes.NAME, "Clash"), AttackCard {
    init {
        addCoins = 2
        special = "Each other player with 5 or more cards in hand reveals all but one, and discards one of them that you choose."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.filter { it.hand.size >= 5 }.forEach { opponent ->
            opponent.addEventLogWithUsername("revealed their hand to ${player.username}")
            val card = opponent.hand.firstOrNull() ?: return@forEach
            opponent.discardCardFromHand(card)
        }
    }

    companion object {
        const val NAME = "Archer"
    }
}

class Warlord : AlliesSplitCard(NAME, CardType.ActionAttackDuration, 5, Clashes.NAME, "Clash"), StartOfTurnDurationAction {
    var isAttackActive = false

    init {
        addActions = 1
        special = "At the start of your next turn, +2 Cards. Until then, other players can’t play an Action from their hand that they have 2 or more copies of in play."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        isAttackActive = true
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCards(2)
        isAttackActive = false
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        isAttackActive = false
    }

    companion object {
        const val NAME = "Warlord"
    }
}

class Territory : AlliesSplitCard(NAME, CardType.Victory, 6, Clashes.NAME, "Clash"),
        VictoryPointsCalculator, AfterCardGainedListenerForSelf {
    init {
        special = "Worth 1 VP per differently named Victory card you have. When you gain this, gain a Gold per empty Supply pile."
        fontSize = 9
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.filter { it.isVictory }.map { it.name }.toSet().size
    }

    override fun afterCardGained(player: Player) {
        repeat(player.game.emptyPileNames.size) {
            player.gainSupplyCard(Gold(), true)
        }
    }

    companion object {
        const val NAME = "Territory"
    }
}

class Forts : AlliesSplitPile(NAME, 3) {
    override val otherCardsInPile: List<Card>
        get() = listOf(Tent(), Garrison(), HillFort(), Stronghold())

    override fun createMultiTypePile(game: Game): List<Card> = listOf(
            Tent(), Tent(), Tent(), Tent(),
            Garrison(), Garrison(), Garrison(), Garrison(),
            HillFort(), HillFort(), HillFort(), HillFort(),
            Stronghold(), Stronghold(), Stronghold(), Stronghold())

    companion object {
        const val NAME = "Forts"
    }
}

class Tent : AlliesSplitCard(NAME, CardType.Action, 3, Forts.NAME, "Fort"), ChoiceActionCard, CardDiscardedFromPlayListener {
    init {
        addCoins = 2
        special = "You may rotate the Forts. When you discard this from play, you may put it onto your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.yesNoChoice(this, "Rotate the Forts?", "rotate")
    }

    override fun onCardDiscarded(player: Player) {
        player.yesNoChoice(this, "Put ${cardNameWithBackgroundColor} onto your deck?", "topDeck")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice != 1) return
        if (info == "rotate") {
            player.rotatePile(Forts.NAME)
        } else if (info == "topDeck") {
            player.removeCardFromDiscard(this)
            player.addCardToTopOfDeck(this)
        }
    }

    companion object {
        const val NAME = "Tent"
    }
}

class Garrison : AlliesSplitCard(NAME, CardType.ActionDuration, 4, Forts.NAME, "Fort"),
        ConditionalDuration, AfterCardGainedListenerForCardsInPlay, StartOfTurnDurationAction {
    private var tokens = 0

    override val isKeepAtEndOfTurn: Boolean
        get() = tokens > 0

    init {
        addCoins = 2
        special = "This turn, when you gain a card, add a token here. At the start of your next turn, remove them for +1 Card each."
        fontSize = 9
    }

    override fun afterCardGained(card: Card, player: Player) {
        tokens++
        player.addEventLogWithUsername("added a token to $cardNameWithBackgroundColor")
    }

    override fun durationStartOfTurnAction(player: Player) {
        if (tokens > 0) {
            player.addEventLogWithUsername("removed $tokens token${if (tokens == 1) "" else "s"} from $cardNameWithBackgroundColor for +$tokens Card${if (tokens == 1) "" else "s"}")
            player.drawCards(tokens)
        }
        tokens = 0
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        tokens = 0
    }

    companion object {
        const val NAME = "Garrison"
    }
}

class HillFort : AlliesSplitCard(NAME, CardType.Action, 5, Forts.NAME, "Fort"), ChooseCardActionCard, ChoiceActionCard {
    private var chosenCard: Card? = null

    init {
        special = "Gain a card costing up to \$4. Choose one: Put it into your hand; or +1 Card and +1 Action."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Gain a card costing up to \$4", this,
                { it.debtCost == 0 && player.getCardCostWithModifiers(it) <= 4 },
                choosingEmptyPilesAllowed = false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        chosenCard = card
        player.makeChoice(this, Choice(1, "Put it into your hand"), Choice(2, "+1 Card and +1 Action"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = chosenCard ?: return
        if (choice == 1) {
            player.gainSupplyCard(card, true, CardLocation.Hand)
        } else {
            player.gainSupplyCard(card, true)
            player.drawCard()
            player.addActions(1)
        }
    }

    companion object {
        const val NAME = "Hill Fort"
    }
}

class Stronghold : AlliesSplitCard(NAME, CardType.ActionDurationVictory, 6, Forts.NAME, "Fort"),
        ChoiceActionCard, ConditionalDuration, StartOfTurnDurationAction {
    private var drawNextTurn = false

    override val isKeepAtEndOfTurn: Boolean
        get() = drawNextTurn

    init {
        victoryPoints = 2
        special = "Choose one: +\$3; or at the start of your next turn, +3 Cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        drawNextTurn = false
        player.makeChoice(this, Choice(1, "+\$3"), Choice(2, "+3 Cards next turn"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addCoins(3)
        } else {
            drawNextTurn = true
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        if (drawNextTurn) {
            player.drawCards(3)
            drawNextTurn = false
        }
    }

    companion object {
        const val NAME = "Stronghold"
    }
}

class Odysseys : AlliesSplitPile(NAME, 3) {
    override val otherCardsInPile: List<Card>
        get() = listOf(OldMap(), Voyage(), SunkenTreasure(), DistantShore())

    override fun createMultiTypePile(game: Game): List<Card> = listOf(
            OldMap(), OldMap(), OldMap(), OldMap(),
            Voyage(), Voyage(), Voyage(), Voyage(),
            SunkenTreasure(), SunkenTreasure(), SunkenTreasure(), SunkenTreasure(),
            DistantShore(), DistantShore(), DistantShore(), DistantShore())

    companion object {
        const val NAME = "Odysseys"
    }
}

class OldMap : AlliesSplitCard(NAME, CardType.Action, 3, Odysseys.NAME, "Odyssey"),
        DiscardCardsForBenefitActionCard, ChoiceActionCard {
    init {
        addCards = 1
        addActions = 1
        special = "Discard a card. +1 Card. You may rotate the Odysseys."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsForBenefit(this, 1, "Discard a card")
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        player.drawCard()
        player.yesNoChoice(this, "Rotate the Odysseys?", "rotate")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "rotate") {
            player.rotatePile(Odysseys.NAME)
        }
    }

    companion object {
        const val NAME = "Old Map"
    }
}

class Voyage : AlliesSplitCard(NAME, CardType.ActionDuration, 4, Odysseys.NAME, "Odyssey"), ConditionalDuration {
    private var triggeredExtraTurn = false

    override val isKeepAtEndOfTurn: Boolean
        get() = triggeredExtraTurn

    init {
        addActions = 1
        special = "Take an extra turn after this one, during which you can only play 3 cards from your hand."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        triggeredExtraTurn = !player.game.isExtraTurnForCurrentPlayer
        if (triggeredExtraTurn) {
            player.game.isExtraTurnForCurrentPlayer = true
            player.maxCardsToPlayFromHandNextTurn = 3
            player.addEventLogWithUsername("will take an extra turn from ${cardNameWithBackgroundColor}")
        }
    }

    override fun removedFromPlay(player: Player) {
        super.removedFromPlay(player)
        triggeredExtraTurn = false
    }

    companion object {
        const val NAME = "Voyage"
    }
}

class SunkenTreasure : AlliesSplitCard(NAME, CardType.Treasure, 5, Odysseys.NAME, "Odyssey") {
    init {
        special = "Gain an Action card you don’t have a copy of in play."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGain({ card -> card.isAction && player.inPlay.none { it.name == card.name } },
                "Gain an Action card you don’t have a copy of in play")
    }

    companion object {
        const val NAME = "Sunken Treasure"
    }
}

class DistantShore : AlliesSplitCard(NAME, CardType.ActionVictory, 6, Odysseys.NAME, "Odyssey") {
    init {
        addCards = 2
        addActions = 1
        victoryPoints = 2
        special = "Gain an Estate."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Estate(), true)
    }

    companion object {
        const val NAME = "Distant Shore"
    }
}

class Townsfolk : AlliesSplitPile(NAME, 2) {
    override val otherCardsInPile: List<Card>
        get() = listOf(TownCrier(), Blacksmith(), Miller(), Elder())

    override fun createMultiTypePile(game: Game): List<Card> = listOf(
            TownCrier(), TownCrier(), TownCrier(), TownCrier(),
            Blacksmith(), Blacksmith(), Blacksmith(), Blacksmith(),
            Miller(), Miller(), Miller(), Miller(),
            Elder(), Elder(), Elder(), Elder())

    companion object {
        const val NAME = "Townsfolk"
    }
}

class TownCrier : AlliesSplitCard(NAME, CardType.Action, 2, Townsfolk.NAME, "Townsfolk"), ChoiceActionCard {
    init {
        special = "Choose one: +\$2; or gain a Silver; or +1 Card and +1 Action. You may rotate the Townsfolk."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "+\$2"), Choice(2, "Gain a Silver"), Choice(3, "+1 Card and +1 Action"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (info == "rotate") {
            if (choice == 1) {
                player.rotatePile(Townsfolk.NAME)
            }
            return
        }

        when (choice) {
            1 -> player.addCoins(2)
            2 -> player.gainSupplyCard(Silver(), true)
            3 -> {
                player.drawCard()
                player.addActions(1)
            }
        }
        player.yesNoChoice(this, "Rotate the Townsfolk?", "rotate")
    }

    companion object {
        const val NAME = "Town Crier"
    }
}

class Blacksmith : AlliesSplitCard(NAME, CardType.Action, 3, Townsfolk.NAME, "Townsfolk"), ChoiceActionCard {
    init {
        special = "Choose one: Draw until you have 6 cards in hand; or +2 Cards; or +1 Card and +1 Action."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "Draw to 6"), Choice(2, "+2 Cards"), Choice(3, "+1 Card and +1 Action"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> if (player.hand.size < 6) player.drawCards(6 - player.hand.size)
            2 -> player.drawCards(2)
            3 -> {
                player.drawCard()
                player.addActions(1)
            }
        }
    }

    companion object {
        const val NAME = "Blacksmith"
    }
}

class Miller : AlliesSplitCard(NAME, CardType.Action, 4, Townsfolk.NAME, "Townsfolk"), ChooseCardActionCard {
    private val revealedCards = mutableListOf<Card>()

    init {
        addActions = 1
        special = "Look at the top 4 cards of your deck. Put one into your hand and discard the rest."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        revealedCards.clear()
        revealedCards.addAll(player.removeTopCardsOfDeck(4, revealCards = true))
        if (revealedCards.isNotEmpty()) {
            player.chooseCardAction("Put one into your hand and discard the rest", this, revealedCards, false)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        revealedCards.remove(card)
        player.addCardToHand(card, true)
        player.addCardsToDiscard(revealedCards, true)
        revealedCards.clear()
    }

    companion object {
        const val NAME = "Miller"
    }
}

class Elder : AlliesSplitCard(NAME, CardType.Action, 5, Townsfolk.NAME, "Townsfolk"), ChooseCardActionCard, ChoiceActionCard {
    init {
        addCoins = 2
        special = "You may play an Action card from your hand. When it gives you a choice of abilities this turn, you may choose an extra different option."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isAction }) {
            player.yesNoChoice(this, "Play an Action card from your hand?", "play")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "play") {
            player.chooseCardFromHand("Play an Action card from your hand", this) { it.isAction }
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.playCard(card)
    }

    companion object {
        const val NAME = "Elder"
    }
}

class Wizards : AlliesSplitPile(NAME, 3) {
    override val otherCardsInPile: List<Card>
        get() = listOf(Student(), Conjurer(), Sorcerer(), Lich())

    override fun createMultiTypePile(game: Game): List<Card> = listOf(
            Student(), Student(), Student(), Student(),
            Conjurer(), Conjurer(), Conjurer(), Conjurer(),
            Sorcerer(), Sorcerer(), Sorcerer(), Sorcerer(),
            Lich(), Lich(), Lich(), Lich())

    companion object {
        const val NAME = "Wizards"
    }
}

class Student : AlliesSplitCard(NAME, CardType.Action, 3, Wizards.NAME, "Wizard", "Liaison"),
        ChoiceActionCard, TrashCardsForBenefitActionCard {
    init {
        addActions = 1
        special = "You may rotate the Wizards. Trash a card from your hand. If it’s a Treasure, +1 Favor and put this onto your deck."
        fontSize = 9
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.yesNoChoice(this, "Rotate the Wizards?", "rotate")
        player.trashCardsFromHandForBenefit(this, 1)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && info == "rotate") {
            player.rotatePile(Wizards.NAME)
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.firstOrNull()?.isTreasure == true) {
            player.addFavors(1)
            player.removeCardInPlay(this, CardLocation.Deck)
            player.addCardToTopOfDeck(this)
        }
    }

    companion object {
        const val NAME = "Student"
    }
}

class Conjurer : AlliesSplitCard(NAME, CardType.ActionDuration, 4, Wizards.NAME, "Wizard"), StartOfTurnDurationAction {
    init {
        special = "Gain a card costing up to \$4. At the start of your next turn, put this into your hand."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainWithMaxCost(4)
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.removeDurationCardInPlay(this, CardLocation.Hand)
        player.addCardToHand(this, true)
    }

    companion object {
        const val NAME = "Conjurer"
    }
}

class Sorcerer : AlliesSplitCard(NAME, CardType.ActionAttack, 5, Wizards.NAME, "Wizard"), AttackCard, ChooseCardActionCard {
    init {
        addCards = 1
        addActions = 1
        special = "Each other player names a card, then reveals the top card of their deck. If wrong, they gain a Curse."
        isCurseGiver = true
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            opponent.chooseCardAction("Name a card for ${player.username}'s ${cardNameWithBackgroundColor}", this, opponent.game.allCardsCopy, false)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addEventLogWithUsername("named ${card.cardNameWithBackgroundColor}")
        val revealed = player.revealTopCardOfDeck()
        if (revealed != null && revealed.name != card.name && player.game.isCardAvailableInSupply(Curse())) {
            player.gainSupplyCard(Curse(), true)
        }
    }

    companion object {
        const val NAME = "Sorcerer"
    }
}

class Lich : AlliesSplitCard(NAME, CardType.Action, 6, Wizards.NAME, "Wizard"), AfterCardTrashedListenerForSelf {
    init {
        addCards = 6
        addActions = 2
        special = "Skip a turn. When you trash this, discard it and gain a cheaper card from the trash."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.skipNextTurn = true
    }

    override fun afterCardTrashed(player: Player) {
        player.game.trashedCards.remove(this)
        player.addCardToDiscard(this)
        player.gainCardFromTrash(true) { it.debtCost == 0 && player.getCardCostWithModifiers(it) < player.getCardCostWithModifiers(this) }
    }

    companion object {
        const val NAME = "Lich"
    }
}
