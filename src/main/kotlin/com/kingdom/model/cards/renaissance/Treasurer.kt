package com.kingdom.model.cards.renaissance

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Treasurer : RenaissanceCard(NAME, CardType.Action, 5), ChoiceActionCard {

    init {
        disabled = true
        addCoins = 3
        special = "Choose one: Trash a Treasure from your hand; or gain a Treasure from the trash to your hand; or take the Key."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val choices = mutableListOf<Choice>()

        if (player.hand.any { it.isTreasure }) {
            choices.add(Choice(1, "Trash Treasure"))
        }

        if (player.game.trashedCards.any { it.isTreasure }) {
            choices.add(Choice(2, "Treasure from trash"))
        }

        choices.add(Choice(3, "Take Key"))

        if (choices.size == 1) {
            takeKey(player)
        } else {
            player.makeChoiceFromList(this, choices)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> player.trashCardFromHand(false) { c -> c.isTreasure }
            2 -> {
                player.isNextCardToHand = true
                player.gainCardFromTrash(false) { c -> c.isTreasure }
            }
            3 -> takeKey(player)
        }
    }

    private fun takeKey(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Treasurer"
    }
}