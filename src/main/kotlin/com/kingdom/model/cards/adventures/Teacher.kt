package com.kingdom.model.cards.adventures

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.players.Player

class Teacher : AdventuresCard(NAME, CardType.ActionReserve, 6), TavernCard, ChoiceActionCard, ChooseCardActionCard {

    init {
        special = "Put this on your Tavern mat. At the start of your turn, you may call this, to move your +1 Card, +1 Action, +1 Buy, or +\$1 token to an Action Supply pile you have no tokens on. (When you play a card from that pile, you first get that bonus.) (This is not in the Supply.)"
        textSize = 114
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.moveCardInPlayToTavern(this)
    }

    override fun isTavernCardActionable(player: Player): Boolean {
        return player.isStartOfTurn
    }

    override fun onTavernCardCalled(player: Player) {
        player.makeChoice(this, "Choose which bonus token to move:", Choice(1, "+1 Card"), Choice(2, "+1 Action"), Choice(3, "+1 Buy"), Choice(4, "+\$1"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        player.chooseCardFromSupply("Choose which supply pile to put the token on", this, { c -> !player.supplyPilesWithBonusTokens.contains(c.name) }, choice)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        val tokenChoice = info as Int

        when (tokenChoice) {
            1 -> player.plusCardTokenSupplyPile = card.name
            2 -> player.plusActionTokenSupplyPile = card.name
            3 -> player.plusBuyTokenSupplyPile = card.name
            4 -> player.plusCoinTokenSupplyPile = card.name
        }
    }

    companion object {
        const val NAME: String = "Teacher"
    }
}

