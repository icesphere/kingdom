package com.kingdom.model.cards.risingsun

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.base.Militia
import com.kingdom.model.cards.base.Witch
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player
import com.kingdom.repository.CardRepository

class ApproachingArmy : RisingSunProphecy(NAME), GameSetupModifier {
    init {
        special = "After you play an Attack card, +\$1. Setup: Add an Attack kingdom card pile to the Supply."
    }

    override fun modifyGameSetup(game: Game) {
        val attackPile = listOf(Militia(), Witch()).firstOrNull { attack -> game.kingdomCards.none { it.name == attack.name } } ?: return
        game.addKingdomCardPile(attackPile)
    }

    override fun afterCardPlayed(card: Card, player: Player) {
        if (card.isAttack) {
            player.addCoins(1)
        }
    }

    companion object {
        const val NAME: String = "Approaching Army"
    }
}

class BidingTime : RisingSunProphecy(NAME) {
    init {
        special = "At the start of your Clean-up, set aside your hand face down. At the start of your next turn, put those cards into your hand."
        fontSize = 8
    }

    override fun onStartOfCleanup(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.cardsToPutIntoHandAfterDrawingCardsAtEndOfTurn.addAll(player.hand)
            player.hand.clear()
            player.refreshPlayerHandArea()
        }
    }

    companion object {
        const val NAME: String = "Biding Time"
    }
}

class Bureaucracy : RisingSunProphecy(NAME) {
    init {
        special = "When you gain a card that doesn't cost \$0, gain a Copper."
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.debtCost != 0 || player.getCardCostWithModifiers(card) != 0) {
            player.gainSupplyCard(Copper(), true)
        }
    }

    companion object {
        const val NAME: String = "Bureaucracy"
    }
}

class DivineWind : RisingSunProphecy(NAME) {
    init {
        special = "When you remove the last Sun token, remove all Kingdom card piles from the Supply, and set up 10 new random piles."
        fontSize = 8
    }

    override fun onFulfilled(game: Game) {
        val currentNames = game.kingdomCards.map { it.name }.toSet()
        val replacements = CardRepository().allCards
                .filter { !it.disabled && !currentNames.contains(it.name) }
                .shuffled()
                .take(10)
        if (replacements.size == 10) {
            game.replaceKingdomCardPiles(replacements)
        }
    }

    companion object {
        const val NAME: String = "Divine Wind"
    }
}

class Enlightenment : RisingSunProphecy(NAME) {
    init {
        special = "Treasures are also Actions. When you play a Treasure in an Action phase, instead of following its instructions, +1 Card and +1 Action."
        fontSize = 8
    }

    override fun replaceCardPlayed(card: Card, player: Player): Boolean {
        if (!player.isTreasurePlayedAsActionByProphecy(card)) {
            return false
        }

        player.drawCard()
        player.addActions(1)
        return true
    }

    companion object {
        const val NAME: String = "Enlightenment"
    }
}

class FlourishingTrade : RisingSunProphecy(NAME), CardCostModifier {
    init {
        special = "Cards cost \$1 less. You may use Action plays as Buys."
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int = -1

    companion object {
        const val NAME: String = "Flourishing Trade"
    }
}

class GoodHarvest : RisingSunProphecy(NAME) {
    init {
        special = "The first time you play each differently named Treasure each turn, first, +1 Buy and +\$1."
        fontSize = 8
    }

    override fun beforeCardPlayed(card: Card, player: Player) {
        if (card.isTreasure && player.recordGoodHarvestTreasure(card)) {
            player.addBuys(1)
            player.addCoins(1)
        }
    }

    companion object {
        const val NAME: String = "Good Harvest"
    }
}

class GreatLeader : RisingSunProphecy(NAME) {
    init {
        special = "After each Action card you play, +1 Action."
    }

    override fun afterCardPlayed(card: Card, player: Player) {
        if (player.isActionForCurrentGame(card)) {
            player.addActions(1)
        }
    }

    companion object {
        const val NAME: String = "Great Leader"
    }
}

class Growth : RisingSunProphecy(NAME) {
    init {
        special = "When you gain a Treasure, gain a cheaper card."
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (!card.isTreasure) {
            return
        }

        val maxCost = player.getCardCostWithModifiers(card) - 1
        if (maxCost >= 0 && player.game.availableCards.any { it.debtCost == 0 && player.getCardCostWithModifiers(it) <= maxCost }) {
            player.chooseSupplyCardToGainWithMaxCost(maxCost, text = "Gain a cheaper card due to ${cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Growth"
    }
}

class HarshWinter : RisingSunProphecy(NAME) {
    init {
        special = "When you gain a card on your turn, if there's debt on its pile, take it; otherwise put 2 debt on its pile."
        fontSize = 8
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (!player.isYourTurn) {
            return
        }

        val debtOnPile = player.game.getDebtOnSupplyPile(card.pileName)
        if (debtOnPile > 0) {
            player.addDebt(debtOnPile)
            player.game.clearDebtFromSupplyPile(card.pileName)
        } else {
            player.game.addDebtToSupplyPile(card.pileName, 2)
        }
    }

    companion object {
        const val NAME: String = "Harsh Winter"
    }
}

class KindEmperor : RisingSunProphecy(NAME) {
    init {
        special = "At the start of your turn, and when you remove the last Sun token: Gain an Action to your hand."
        fontSize = 8
    }

    override fun applyFulfilledEffect(player: Player) {
        gainAction(player)
    }

    override fun onStartOfTurn(player: Player) {
        gainAction(player)
    }

    private fun gainAction(player: Player) {
        player.chooseSupplyCardToGain({ player.isActionForCurrentGame(it) }, "Gain an Action to your hand", CardLocation.Hand)
    }

    companion object {
        const val NAME: String = "Kind Emperor"
    }
}

class Panic : RisingSunProphecy(NAME) {
    init {
        special = "When you play a Treasure, +2 Buys, and when you discard one from play, return it to its pile."
        fontSize = 8
    }

    override fun beforeCardPlayed(card: Card, player: Player) {
        if (card.isTreasure) {
            player.addBuys(2)
        }
    }

    override fun handleDiscardFromPlay(card: Card, player: Player): Boolean {
        if (!card.isTreasure || !player.game.numInPileMap.containsKey(card.pileName)) {
            return false
        }

        player.cardRemovedFromPlay(card, CardLocation.Supply)
        player.game.returnCardToSupply(card)
        return true
    }

    companion object {
        const val NAME: String = "Panic"
    }
}

class Progress : RisingSunProphecy(NAME) {
    init {
        special = "When you gain a card, put it onto your deck."
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        player.isNextCardToHand = false
        player.isNextCardToTopOfDeck = true
        return false
    }

    companion object {
        const val NAME: String = "Progress"
    }
}

class RapidExpansion : RisingSunProphecy(NAME) {
    init {
        special = "When you gain an Action or Treasure, set it aside, and play it at the start of your next turn."
        fontSize = 8
    }

    override fun onCardGained(card: Card, player: Player): Boolean {
        if (!player.isActionForCurrentGame(card) && !card.isTreasure) {
            return false
        }

        player.cardsToPlayAtStartOfNextTurn.add(card)
        player.cardRemovedFromPlay(card, CardLocation.SetAside)
        player.refreshPlayerHandArea()
        return true
    }

    companion object {
        const val NAME: String = "Rapid Expansion"
    }
}

class Sickness : RisingSunProphecy(NAME), ChoiceActionCard {
    init {
        special = "At the start of your turn, choose one: Gain a Curse onto your deck; or discard 3 cards."
        fontSize = 8
    }

    override fun onStartOfTurn(player: Player) {
        player.makeChoice(this,
                "Choose one for ${cardNameWithBackgroundColor}",
                Choice(1, "Gain a Curse onto your deck"),
                Choice(2, "Discard 3 cards"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.gainSupplyCard(Curse(), true, CardLocation.Deck)
        } else {
            player.discardCardsFromHand(3, false)
        }
    }

    companion object {
        const val NAME: String = "Sickness"
    }
}
