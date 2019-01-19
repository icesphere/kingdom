package com.kingdom.model.cards.intrigue

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Lurker : IntrigueCard(NAME, CardType.Action, 2), ChoiceActionCard {
    init {
        addActions = 1
        special = "Choose one: Trash an Action card from the Supply; or gain an Action card from the trash."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val actionCardsInTrash = player.game.trashedCards.filter { it.isAction }
        if (actionCardsInTrash.isNotEmpty()) {
            player.makeChoice(this,
                    "Action cards in the trash: ${actionCardsInTrash.groupedString}",
                    Choice(1, "Trash an Action card from the Supply"),
                    Choice(2, "Gain an Action card from the trash")
            )
        } else {
            player.trashCardFromSupply(false, { c -> c.isAction })
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (choice) {
            1 -> {
                player.trashCardFromSupply(false, { c -> c.isAction })
            }
            2 -> {
                player.gainCardFromTrash(false, { c -> c.isAction })
            }
        }
    }

    companion object {
        const val NAME: String = "Lurker"
    }
}

