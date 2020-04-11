package com.kingdom.model.cards.menagerie

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Wayfarer : MenagerieCard(NAME, CardType.Action, 6), GameSetupModifier, CardCostModifier, ChoiceActionCard {

    init {
        addCards = 3
        special = "You may gain a Silver. This has the same cost as the last other card gained this turn, if any."
    }

    override fun modifyGameSetup(game: Game) {
        game.gameCardCostModifiers.add(this)
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.yesNoChoice(this, "Gain a ${Silver().cardNameWithBackgroundColor}?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.gainSupplyCard(Silver(), true)
        }
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        if (card.name != name) {
            return 0
        }

        val lastCardGainThatIsNotWayfarer = player.game.currentPlayer.cardsGained.reversed().firstOrNull { it.name != name }
        if (lastCardGainThatIsNotWayfarer != null) {
            return player.getCardCostWithModifiers(lastCardGainThatIsNotWayfarer) - 6
        }

        return 0
    }

    companion object {
        const val NAME: String = "Wayfarer"
    }
}

