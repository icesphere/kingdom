package com.kingdom.model.cards.adventures

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Miser : AdventuresCard(NAME, CardType.Action, 4), GameSetupModifier, ChoiceActionCard {

    init {
        special = "Choose one: Put a Copper from your hand onto your Tavern mat; or +\$1 per Copper on your Tavern mat."
        isAddCoinsCard = true
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowTavern = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val coppersOnTavern = player.tavernCards.count { it.isCopper }
        if (player.hand.any { it.isCopper }) {
            player.makeChoice(this, Choice(1, "Copper from hand to Tavern"), Choice(2, "+\$$coppersOnTavern"))
        } else {
            if (coppersOnTavern > 0) {
                player.addCoins(coppersOnTavern)
                player.addEventLogWithUsername("Gained +\$$coppersOnTavern from ${this.cardNameWithBackgroundColor}")
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val copper = player.hand.first { it.isCopper }
            player.addEventLogWithUsername("Put ${copper.cardNameWithBackgroundColor} from hand onto Tavern")
            player.tavernCards.add(copper)
            player.removeCardFromHand(copper)
        } else {
            val coppersOnTavern = player.tavernCards.count { it.isCopper }
            player.addCoins(coppersOnTavern)
            player.addEventLogWithUsername("Gained +\$$coppersOnTavern from ${this.cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Miser"
    }
}

