package com.kingdom.model.cards.darkages

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Duchy
import com.kingdom.model.players.Player

class Count : DarkAgesCard(NAME, CardType.Action, 5), ChoiceActionCard {

    init {
        special = "Choose one: Discard 2 cards; or put a card from your hand onto your deck; or gain a Copper. Choose one: +\$3; trash your hand; or gain a Duchy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoiceWithInfo(this, "", true, Choice(1, "Discard 2 cards"), Choice(2, "Put a card from your hand onto your deck"), Choice(3, "Gain a Copper"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val firstChoice = info as Boolean
        if (firstChoice) {
            when (choice) {
                1 -> player.discardCardsFromHand(2, false)
                2 -> player.addCardFromHandToTopOfDeck()
                3 -> player.gainSupplyCard(Copper(), true)
            }
            player.makeChoiceWithInfo(this, "", false, Choice(1, "+\$3"), Choice(2, "Trash your hand"), Choice(3, "Gain a Duchy"))
        } else {
            when (choice) {
                1 -> player.addCoins(3)
                2 -> player.trashHand()
                3 -> player.gainSupplyCard(Duchy(), true)
            }
        }
    }

    companion object {
        const val NAME: String = "Count"
    }
}

