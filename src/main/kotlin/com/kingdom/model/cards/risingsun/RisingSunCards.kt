package com.kingdom.model.cards.risingsun

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.PermanentDuration
import com.kingdom.model.cards.Shadow
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.base.CouncilRoom
import com.kingdom.model.cards.base.Festival
import com.kingdom.model.cards.base.Laboratory
import com.kingdom.model.cards.base.Market
import com.kingdom.model.cards.listeners.StartOfCleanupListener
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Alley : RisingSunCard(NAME, CardType.Action, 4, shadow = true), Shadow {
    init {
        addCards = 1
        addActions = 1
        special = "Discard a card. You can play this from your deck as if in your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsFromHand(1, false)
    }

    companion object {
        const val NAME: String = "Alley"
    }
}

class Aristocrat : RisingSunCard(NAME, CardType.Action, 3) {
    init {
        special = "If the number of Aristocrats you have in play is 1 or 5: +3 Actions; 2 or 6: +3 Cards; 3 or 7: +\$3; 4 or 8: +3 Buys."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        when (player.inPlayWithDuration.count { it.name == NAME }) {
            1, 5 -> player.addActions(3)
            2, 6 -> player.drawCards(3)
            3, 7 -> player.addCoins(3)
            4, 8 -> player.addBuys(3)
        }
    }

    companion object {
        const val NAME: String = "Aristocrat"
    }
}

class Artist : RisingSunCard(NAME, CardType.Action, 0, debtCost = 8) {
    init {
        addActions = 1
        special = "+1 Card per card you have exactly one copy of in play."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val uniqueInPlay = player.inPlayWithDuration.groupingBy { it.name }.eachCount().count { it.value == 1 }
        player.drawCards(uniqueInPlay)
    }

    companion object {
        const val NAME: String = "Artist"
    }
}

class Change : RisingSunCard(NAME, CardType.Action, 4), ChooseCardsActionCard, ChooseCardActionCard {
    private var trashedCardCost = 0

    init {
        special = "If you have any debt, +\$3. Otherwise, trash a card from your hand, and gain a card costing more than it. Take debt equal to the difference."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.debt > 0) {
            player.addCoins(3)
        } else if (player.hand.isNotEmpty()) {
            player.chooseCardsFromHand("Trash a card from your hand", 1, false, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        if (cards.isEmpty()) {
            return
        }

        val trashedCard = cards.first()
        trashedCardCost = player.getCardCostWithModifiers(trashedCard)
        player.trashCardFromHand(trashedCard)
        player.chooseCardFromSupply("Gain a card costing more than ${trashedCard.cardNameWithBackgroundColor}", this, { card ->
            card.debtCost == 0 && player.getCardCostWithModifiers(card) > trashedCardCost
        }, choosingEmptyPilesAllowed = false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        val gainedCost = player.getCardCostWithModifiers(card)
        player.gainSupplyCard(card, true)
        player.addDebt(gainedCost - trashedCardCost)
    }

    companion object {
        const val NAME: String = "Change"
    }
}

class Craftsman : RisingSunCard(NAME, CardType.Action, 3) {
    init {
        special = "+2 debt. Gain a card costing up to \$5."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addDebt(2)
        player.chooseSupplyCardToGainWithMaxCost(5)
    }

    companion object {
        const val NAME: String = "Craftsman"
    }
}

class Daimyo : RisingSunCard(NAME, CardType.Action, 0, debtCost = 6, command = true) {
    init {
        addCards = 1
        addActions = 1
        special = "The next time you play a non-Command Action card this turn, replay it afterwards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.numDaimyosActive++
    }

    companion object {
        const val NAME: String = "Daimyo"
    }
}

class Fishmonger : RisingSunCard(NAME, CardType.Action, 2, shadow = true), Shadow {
    init {
        addBuys = 1
        addCoins = 1
        special = "You can play this from your deck as if in your hand."
    }

    companion object {
        const val NAME: String = "Fishmonger"
    }
}

class GoldMine : RisingSunCard(NAME, CardType.Action, 5) {
    init {
        addCards = 1
        addActions = 1
        addBuys = 1
        special = "You may gain a Gold and get +4 debt."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val gold = Gold()
        if (!player.game.isCardAvailableInSupply(gold)) {
            return
        }

        player.yesNoChoice(object : ChoiceActionCard {
            override val name: String = "GoldMineGainGold"

            override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                if (choice == 1) {
                    player.gainSupplyCard(Gold(), true)
                    player.addDebt(4)
                }
            }
        }, "Gain a Gold and get +4 debt?")
    }

    companion object {
        const val NAME: String = "Gold Mine"
    }
}

class ImperialEnvoy : RisingSunCard(NAME, CardType.Action, 5) {
    init {
        addCards = 5
        addBuys = 1
        special = "+2 debt."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addDebt(2)
    }

    companion object {
        const val NAME: String = "Imperial Envoy"
    }
}

class Kitsune : RisingSunCard(NAME, CardType.ActionAttack, 5, omen = true), AttackCard, ChoiceActionCard {
    init {
        special = "Choose two different options: +2 Actions; +2 debt; each other player gains a Curse; gain a Silver."
        isCurseGiver = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        chooseNext(player, mutableListOf())
    }

    private fun chooseNext(player: Player, selected: MutableList<Int>) {
        val choices = listOf(
                Choice(1, "+2 Actions"),
                Choice(2, "+2 debt"),
                Choice(3, "Each other player gains a Curse"),
                Choice(4, "Gain a Silver")
        ).filterNot { selected.contains(it.choiceNumber) }

        player.makeChoiceFromListWithInfo(this, "Choose ${if (selected.isEmpty()) "first" else "second"} Kitsune option", selected, choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val selected = info as MutableList<Int>
        selected.add(choice)

        if (selected.size < 2) {
            chooseNext(player, selected)
            return
        }

        selected.sorted().forEach {
            when (it) {
                1 -> player.addActions(2)
                2 -> player.addDebt(2)
                3 -> player.triggerAttack(this)
                4 -> player.gainSupplyCard(Silver(), true)
            }
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            opponent.gainSupplyCard(Curse(), true)
        }
    }

    companion object {
        const val NAME: String = "Kitsune"
    }
}

class Litter : RisingSunCard(NAME, CardType.Action, 5) {
    init {
        addCards = 2
        addActions = 2
        special = "+1 debt."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addDebt(1)
    }

    companion object {
        const val NAME: String = "Litter"
    }
}

class MountainShrine : RisingSunCard(NAME, CardType.Action, 0, debtCost = 5, omen = true), ChooseCardsActionCard {
    init {
        special = "+2 debt. You may trash a card from your hand. Then if there are any Action cards in the trash, +2 Cards."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addDebt(2)
        if (player.hand.isEmpty()) {
            drawIfActionInTrash(player)
        } else {
            player.chooseCardsFromHand("You may trash a card from your hand", 1, true, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        if (cards.isNotEmpty()) {
            player.trashCardFromHand(cards.first())
        }
        drawIfActionInTrash(player)
    }

    private fun drawIfActionInTrash(player: Player) {
        if (player.game.trashedCards.any { player.isActionForCurrentGame(it) }) {
            player.drawCards(2)
        }
    }

    companion object {
        const val NAME: String = "Mountain Shrine"
    }
}

class Ninja : RisingSunCard(NAME, CardType.ActionAttack, 4, shadow = true), AttackCard, Shadow {
    init {
        addCards = 1
        special = "Each other player discards down to 3 cards in hand. You can play this from your deck as if in your hand."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            if (opponent.hand.size > 3) {
                opponent.discardCardsFromHand(opponent.hand.size - 3, false)
            }
        }
    }

    companion object {
        const val NAME: String = "Ninja"
    }
}

class Poet : RisingSunCard(NAME, CardType.Action, 4, omen = true) {
    init {
        addCards = 1
        addActions = 1
        special = "Reveal the top card of your deck. If it costs \$3 or less, put it into your hand."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val card = player.removeTopCardOfDeck() ?: return
        player.addEventLogWithUsername("revealed ${card.cardNameWithBackgroundColor}")
        if (card.debtCost == 0 && player.getCardCostWithModifiers(card) <= 3) {
            player.addCardToHand(card, true)
        } else {
            player.addCardToTopOfDeck(card, false)
        }
    }

    companion object {
        const val NAME: String = "Poet"
    }
}

class Rice : RisingSunCard(NAME, CardType.Treasure, 7) {
    init {
        addBuys = 1
        special = "+\$1 per different type among cards you have in play."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val numTypes = player.inPlayWithDuration.flatMap { it.typeNames }.toSet().size
        player.addCoins(numTypes)
    }

    companion object {
        const val NAME: String = "Rice"
    }
}

class RiceBroker : RisingSunCard(NAME, CardType.Action, 5), ChooseCardsActionCard {
    init {
        addActions = 1
        special = "Trash a card from your hand. If it's a Treasure, +2 Cards. If it's an Action, +5 Cards."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.chooseCardsFromHand("Trash a card from your hand", 1, false, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        if (cards.isEmpty()) {
            return
        }

        val trashedCard = cards.first()
        player.trashCardFromHand(trashedCard)
        if (trashedCard.isTreasure) {
            player.drawCards(2)
        }
        if (player.isActionForCurrentGame(trashedCard)) {
            player.drawCards(5)
        }
    }

    companion object {
        const val NAME: String = "Rice Broker"
    }
}

class RiverShrine : RisingSunCard(NAME, CardType.Action, 4, omen = true), ChooseCardsActionCard, StartOfCleanupListener {
    init {
        special = "Trash up to 2 cards from your hand. At the start of Clean-up, if you didn't gain any cards in your Buy phase this turn, gain a card costing up to \$4."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.chooseCardsFromHand("Trash up to 2 cards from your hand", 2, true, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        cards.forEach { player.trashCardFromHand(it) }
    }

    override fun onStartOfCleanup(player: Player) {
        if (!player.gainedCardInBuyPhaseThisTurn) {
            player.chooseSupplyCardToGainWithMaxCost(4)
        }
    }

    companion object {
        const val NAME: String = "River Shrine"
    }
}

class Riverboat : RisingSunCard(NAME, CardType.ActionDuration, 3), GameSetupModifier, StartOfTurnDurationAction {
    private var setAsideCard: Card = Market()

    init {
        associatedCards.add(setAsideCard)
        special = "At the start of your next turn, play the set-aside card, leaving it there. Setup: Set aside an unused non-Duration Action card costing \$5."
        fontSize = 8
    }

    override fun modifyGameSetup(game: Game) {
        setAsideCard = listOf(Market(), Laboratory(), Festival(), CouncilRoom())
                .firstOrNull { candidate -> game.kingdomCards.none { it.name == candidate.name } }
                ?: Market()
        associatedCards.clear()
        associatedCards.add(setAsideCard)
    }

    override fun durationStartOfTurnAction(player: Player) {
        val cardToPlay = setAsideCard.copy(false)
        player.addEventLogWithUsername("played ${cardToPlay.cardNameWithBackgroundColor} from ${cardNameWithBackgroundColor}")
        player.addActions(1)
        player.playCard(cardToPlay, repeatedAction = true, showLog = false)
    }

    companion object {
        const val NAME: String = "Riverboat"
    }
}

class Ronin : RisingSunCard(NAME, CardType.Action, 5, shadow = true), Shadow {
    init {
        special = "Draw until you have 7 cards in hand. You can play this from your deck as if in your hand."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        while (player.hand.size < 7) {
            val before = player.hand.size
            player.drawCard()
            if (player.hand.size == before) {
                return
            }
        }
    }

    companion object {
        const val NAME: String = "Ronin"
    }
}

class RootCellar : RisingSunCard(NAME, CardType.Action, 3) {
    init {
        addCards = 3
        addActions = 1
        special = "+3 debt."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addDebt(3)
    }

    companion object {
        const val NAME: String = "Root Cellar"
    }
}

class RusticVillage : RisingSunCard(NAME, CardType.Action, 4, omen = true), ChooseCardsActionCard {
    init {
        addCards = 1
        addActions = 2
        special = "You may discard 2 cards for +1 Card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.size >= 2) {
            player.chooseCardsFromHand("You may discard 2 cards for +1 Card", 2, true, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        if (cards.size == 2) {
            cards.forEach { player.discardCardFromHand(it) }
            player.drawCard()
        }
    }

    companion object {
        const val NAME: String = "Rustic Village"
    }
}

class Samurai : RisingSunCard(NAME, CardType.ActionAttackDuration, 6), AttackCard, StartOfTurnDurationAction, PermanentDuration {
    init {
        special = "Each other player discards down to 3 cards in hand once. At the start of each of your turns this game, +\$1. This stays in play."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            if (opponent.hand.size > 3) {
                opponent.discardCardsFromHand(opponent.hand.size - 3, false)
            }
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addCoins(1)
    }

    companion object {
        const val NAME: String = "Samurai"
    }
}

class SnakeWitch : RisingSunCard(NAME, CardType.ActionAttack, 2), AttackCard {
    init {
        addCards = 1
        addActions = 1
        special = "If your hand has no duplicate cards, you may reveal it and return this to its pile, to have each other player gain a Curse."
        isCurseGiver = true
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.distinctBy { it.name }.size != player.hand.size) {
            return
        }

        player.yesNoChoice(object : ChoiceActionCard {
            override val name: String = "SnakeWitchRevealHand"

            override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                val snakeWitch = info as SnakeWitch
                if (choice == 1 && player.inPlay.contains(snakeWitch)) {
                    player.addEventLogWithUsername("revealed ${player.hand.groupedString}")
                    player.removeCardInPlay(snakeWitch, CardLocation.Supply)
                    player.game.returnCardToSupply(snakeWitch)
                    player.triggerAttack(snakeWitch)
                }
            }
        }, "Reveal your hand and return ${cardNameWithBackgroundColor} to its pile?", this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            opponent.gainSupplyCard(Curse(), true)
        }
    }

    companion object {
        const val NAME: String = "Snake Witch"
    }
}

class Tanuki : RisingSunCard(NAME, CardType.Action, 5, shadow = true), Shadow, ChooseCardsActionCard {
    init {
        special = "Trash a card from your hand. Gain a card costing up to \$2 more than it. You can play this from your deck as if in your hand."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.chooseCardsFromHand("Trash a card from your hand", 1, false, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        if (cards.isEmpty()) {
            return
        }

        val trashedCard = cards.first()
        val maxCost = player.getCardCostWithModifiers(trashedCard) + 2
        player.trashCardFromHand(trashedCard)
        player.chooseSupplyCardToGainWithMaxCost(maxCost)
    }

    companion object {
        const val NAME: String = "Tanuki"
    }
}

class TeaHouse : RisingSunCard(NAME, CardType.Action, 5, omen = true) {
    init {
        addCards = 1
        addActions = 1
        special = "+2 debt."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addDebt(2)
    }

    companion object {
        const val NAME: String = "Tea House"
    }
}
