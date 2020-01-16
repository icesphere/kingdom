package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.StartOfTurnTavernCard
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.players.Player

class Guide : AdventuresCard(NAME, CardType.ActionReserve, 3), TavernCard, StartOfTurnTavernCard, ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        special = "Put this on your Tavern mat. At the start of your turn, you may call this, to discard your hand and draw 5 cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.moveCardInPlayToTavern(this)
    }

    override fun isTavernCardActionable(player: Player): Boolean {
        return player.isStartOfTurn
    }

    override fun onTavernCardCalled(player: Player) {
        player.discardHand()
        player.drawCards(5)
    }

    override fun onStartOfTurn(player: Player) {
        player.yesNoChoice(this, "Use $cardNameWithBackgroundColor to discard your hand and draw 5 cards?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.callTavernCard(this)
        }
    }

    companion object {
        const val NAME: String = "Guide"
    }
}

