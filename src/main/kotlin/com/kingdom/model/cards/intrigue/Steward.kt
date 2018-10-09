package com.kingdom.model.cards.intrigue

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Steward : IntrigueCard(NAME, CardType.Action, 3), ChoiceActionCard {

    init {
        testing = true
        special = "Choose one: +2 Cards; or +\$2; or trash 2 cards from your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this,
                Choice(1, "+2 Cards"),
                Choice(2, "+\$2"),
                Choice(3, "Trash 2 cards from your hand")
        )
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        when (choice) {
            1 -> {
                player.drawCards(2)
            }
            2 -> {
                player.addCoins(2)
            }
            3 -> {
                player.trashCardsFromHand(2, false)
            }
        }
    }

    companion object {
        const val NAME: String = "Pawn"
    }
}

