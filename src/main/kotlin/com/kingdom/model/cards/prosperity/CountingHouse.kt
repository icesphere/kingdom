package com.kingdom.model.cards.prosperity

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class CountingHouse : ProsperityCard(NAME, CardType.Action, 5), ChoiceActionCard {

    init {
        special = "Look through your discard pile, reveal any number of Coppers from it, and put them into your hand."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val numCoppersInDiscard = player.cardsInDiscard.count { it.isCopper }

        val choices = mutableListOf<Choice>()

        if (numCoppersInDiscard > 0) {
            for (i in 0..numCoppersInDiscard) {
                choices.add(Choice(i, i.toString()))
            }

            player.makeChoiceFromList(this, "How many Coppers would you like to add to your hand from your discard pile?", choices)
        } else {
            player.game.showInfoMessage(player, "There are no Coppers in your discard pile")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        repeat(choice) {
            val copper = player.cardsInDiscard.first { it.isCopper }
            player.removeCardFromDiscard(copper)
            player.addCardToHand(copper)
        }
    }

    companion object {
        const val NAME: String = "Counting House"
    }
}

