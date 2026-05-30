package com.kingdom.model.cards.allies

import com.kingdom.model.Choice
import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

abstract class AlliesAlly(name: String, favorCost: Int) : Ally(name, Deck.Allies, favorCost)

class ArchitectsGuild : AlliesAlly(NAME, 2) {
    init {
        special = "When you gain a card, you may spend 2 Favors to gain a cheaper non-Victory card."
    }

    override fun allySpecialAction(player: Player) {
        player.chooseSupplyCardToGain({ !it.isVictory && it.debtCost == 0 && player.getCardCostWithModifiers(it) <= 4 },
                "Gain a non-Victory card costing up to \$4")
    }

    companion object {
        const val NAME = "Architects' Guild"
    }
}

class BandOfNomads : AlliesAlly(NAME, 1), ChoiceActionCard {
    init {
        special = "When you gain a card costing \$3 or more, you may spend a Favor, for +1 Card, or +1 Action, or +1 Buy."
    }

    override fun allySpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "+1 Card"), Choice(2, "+1 Action"), Choice(3, "+1 Buy"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> player.drawCard()
            2 -> player.addActions(1)
            3 -> player.addBuys(1)
        }
    }

    companion object {
        const val NAME = "Band of Nomads"
    }
}

class CaveDwellers : AlliesAlly(NAME, 1) {
    init {
        special = "At the start of your turn, you may spend a Favor, to discard a card then draw a card. Repeat as desired."
        fontSize = 10
    }

    override fun allySpecialAction(player: Player) {
        player.discardCardsFromHand(1, false)
        player.drawCard()
    }

    companion object {
        const val NAME = "Cave Dwellers"
    }
}

class CircleOfWitches : AlliesAlly(NAME, 3), AttackCard {
    init {
        special = "After playing a Liaison, you may spend 3 Favors to have each other player gain a Curse."
        isCurseGiver = true
        fontSize = 10
    }

    override fun allySpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.filter { it.game.isCardAvailableInSupply(Curse()) }.forEach {
            it.gainSupplyCard(Curse(), true)
        }
    }

    companion object {
        const val NAME = "Circle of Witches"
    }
}

class CityState : AlliesAlly(NAME, 2), ChooseCardActionCard {
    init {
        special = "When you gain an Action card during your turn, you may spend 2 Favors to play it."
    }

    override fun allySpecialAction(player: Player) {
        val actions = player.cardsInDiscard.filter { it.isAction }
        if (actions.isNotEmpty()) {
            player.chooseCardAction("Play an Action card from your discard pile", this, actions, true)
        }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.playCardFromDiscard(card)
    }

    companion object {
        const val NAME = "City-state"
    }
}

class CoastalHaven : AlliesAlly(NAME, 1) {
    init {
        special = "When discarding your hand in Clean-up, you may spend any number of Favors to keep that many cards in hand for next turn."
        fontSize = 9
    }

    override fun allySpecialAction(player: Player) {
        player.showInfoMessage("$cardNameWithBackgroundColor applies during Clean-up and has no immediate action in this UI.")
    }

    companion object {
        const val NAME = "Coastal Haven"
    }
}

class CraftersGuild : AlliesAlly(NAME, 2) {
    init {
        special = "At the start of your turn, you may spend 2 Favors to gain a card costing up to \$4 onto your deck."
        fontSize = 10
    }

    override fun allySpecialAction(player: Player) {
        player.chooseSupplyCardToGainWithMaxCost(4, text = "Gain a card costing up to \$4 onto your deck", destination = CardLocation.Deck)
    }

    companion object {
        const val NAME = "Crafters' Guild"
    }
}

class DesertGuides : AlliesAlly(NAME, 1) {
    init {
        special = "At the start of your turn, you may spend a Favor to discard your hand and draw 5 cards. Repeat as desired."
        fontSize = 10
    }

    override fun allySpecialAction(player: Player) {
        player.discardHand()
        player.drawCards(5)
    }

    companion object {
        const val NAME = "Desert Guides"
    }
}

class FamilyOfInventors : AlliesAlly(NAME, 1), ChooseCardActionCard {
    init {
        special = "At the start of your Buy phase, you may put a Favor token you have on a non-Victory Supply pile. Cards cost \$1 less per Favor token on their piles."
        fontSize = 8
    }

    override fun allySpecialAction(player: Player) {
        player.chooseCardFromSupply("Choose a non-Victory Supply pile to put a Favor token on", this,
                { !it.isVictory }, choosingEmptyPilesAllowed = false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.minusTwoCostTokenSupplyPile = card.pileName
        player.showInfoMessage("$cardNameWithBackgroundColor is using the existing cost token UI; ${card.name} costs \$2 less this turn.")
        player.refreshSupply()
    }

    companion object {
        const val NAME = "Family of Inventors"
    }
}

class FellowshipOfScribes : AlliesAlly(NAME, 1) {
    init {
        special = "After playing an Action, if you have 4 or fewer cards in hand, you may spend a Favor for +1 Card."
        fontSize = 10
    }

    override fun allySpecialAction(player: Player) {
        if (player.hand.size <= 4) {
            player.drawCard()
        }
    }

    companion object {
        const val NAME = "Fellowship of Scribes"
    }
}

class ForestDwellers : AlliesAlly(NAME, 1), ChooseCardsActionCard {
    private val revealedCards = mutableListOf<Card>()

    init {
        special = "At the start of your turn, you may spend a Favor to look at the top 3 cards of your deck, discard any number and put the rest back in any order."
        fontSize = 8
    }

    override fun allySpecialAction(player: Player) {
        revealedCards.clear()
        revealedCards.addAll(player.removeTopCardsOfDeck(3, revealCards = true))
        if (revealedCards.isNotEmpty()) {
            player.chooseCardsAction(3, "Select cards to put back on top; unselected cards are discarded", this, revealedCards, true)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        val cardsToDiscard = revealedCards - cards.toSet()
        player.addCardsToDiscard(cardsToDiscard, true)
        if (cards.isNotEmpty()) {
            player.putCardsOnTopOfDeckInAnyOrder(cards)
        }
        revealedCards.clear()
    }

    companion object {
        const val NAME = "Forest Dwellers"
    }
}

class GangOfPickpockets : AlliesAlly(NAME, 1) {
    init {
        special = "At the start of your turn, discard down to 4 cards in hand unless you spend a Favor."
    }

    override fun allySpecialAction(player: Player) {
        player.showInfoMessage("Spent a Favor to avoid ${cardNameWithBackgroundColor}.")
    }

    companion object {
        const val NAME = "Gang of Pickpockets"
    }
}

class IslandFolk : AlliesAlly(NAME, 5) {
    init {
        special = "At the end of your turn, you may spend 5 Favors to take an extra turn after this one."
    }

    override fun allySpecialAction(player: Player) {
        if (!player.game.isExtraTurnForCurrentPlayer) {
            player.game.isExtraTurnForCurrentPlayer = true
            player.addEventLogWithUsername("will take an extra turn from ${cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME = "Island Folk"
    }
}

class LeagueOfBankers : AlliesAlly(NAME, 4) {
    override fun useAlly(player: Player) {
        allySpecialAction(player)
    }

    init {
        special = "At the start of your Buy phase, +\$1 per 4 Favors you have."
    }

    override fun allySpecialAction(player: Player) {
        player.addCoins(player.favors / 4)
    }

    companion object {
        const val NAME = "League of Bankers"
    }
}

class LeagueOfShopkeepers : AlliesAlly(NAME, 5) {
    override fun useAlly(player: Player) {
        allySpecialAction(player)
    }

    init {
        special = "After playing a Liaison, if you have 5 or more Favors, +\$1, and if 10 or more, +1 Action and +1 Buy."
        fontSize = 10
    }

    override fun allySpecialAction(player: Player) {
        player.addCoins(1)
        if (player.favors >= 10) {
            player.addActions(1)
            player.addBuys(1)
        }
    }

    companion object {
        const val NAME = "League of Shopkeepers"
    }
}

class MarketTowns : AlliesAlly(NAME, 1), ChooseCardActionCard {
    init {
        special = "At the start of your Buy phase, you may spend a Favor to play an Action card from your hand. Repeat as desired."
        fontSize = 10
    }

    override fun allySpecialAction(player: Player) {
        player.chooseCardFromHand("Play an Action card from your hand", this) { it.isAction }
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.addActions(1)
        player.playCard(card)
    }

    companion object {
        const val NAME = "Market Towns"
    }
}

class MountainFolk : AlliesAlly(NAME, 5) {
    init {
        special = "At the start of your turn, you may spend 5 Favors for +3 Cards."
    }

    override fun allySpecialAction(player: Player) {
        player.drawCards(3)
    }

    companion object {
        const val NAME = "Mountain Folk"
    }
}

class OrderOfAstrologers : AlliesAlly(NAME, 1) {
    init {
        special = "When shuffling, you may pick one card per Favor you spend to go on top."
    }

    override fun allySpecialAction(player: Player) {
        player.showInfoMessage("$cardNameWithBackgroundColor applies when shuffling.")
    }

    companion object {
        const val NAME = "Order of Astrologers"
    }
}

class OrderOfMasons : AlliesAlly(NAME, 1) {
    init {
        special = "When shuffling, you may pick up to 2 cards per Favor you spend to put into your discard pile."
        fontSize = 10
    }

    override fun allySpecialAction(player: Player) {
        player.showInfoMessage("$cardNameWithBackgroundColor applies when shuffling.")
    }

    companion object {
        const val NAME = "Order of Masons"
    }
}

class PeacefulCult : AlliesAlly(NAME, 1) {
    init {
        special = "At the start of your Buy phase, you may spend any number of Favors to trash that many cards from your hand."
        fontSize = 10
        isTrashingCard = true
    }

    override fun allySpecialAction(player: Player) {
        player.trashCardsFromHand(1, false)
    }

    companion object {
        const val NAME = "Peaceful Cult"
    }
}

class PlateauShepherds : AlliesAlly(NAME, 0), VictoryPointsCalculator {
    init {
        special = "When scoring, pair up your Favors with cards you have costing \$2, for 2 VP per pair."
    }

    override fun isAllyActionable(player: Player): Boolean = false

    override fun allySpecialAction(player: Player) {}

    override fun calculatePoints(player: Player): Int {
        return minOf(player.favors, player.allCards.count { player.getCardCostWithModifiers(it) == 2 }) * 2
    }

    companion object {
        const val NAME = "Plateau Shepherds"
    }
}

class TrappersLodge : AlliesAlly(NAME, 1) {
    init {
        special = "When you gain a card, you may spend a Favor to put it onto your deck."
    }

    override fun allySpecialAction(player: Player) {
        player.numCardGainedMayPutOnTopOfDeck++
    }

    companion object {
        const val NAME = "Trappers' Lodge"
    }
}

class WoodworkersGuild : AlliesAlly(NAME, 1), TrashCardsForBenefitActionCard {
    init {
        special = "At the start of your Buy phase, you may spend a Favor to trash an Action card from your hand. If you did, gain an Action card."
        fontSize = 9
        isTrashingCard = true
    }

    override fun allySpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash an Action card from your hand",
                cardActionableExpression = { it.isAction })
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isNotEmpty()) {
            player.chooseSupplyCardToGain({ it.isAction }, "Gain an Action card")
        }
    }

    companion object {
        const val NAME = "Woodworkers' Guild"
    }
}
