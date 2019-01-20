package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class DeathCart : DarkAgesCard(NAME, CardType.ActionLooter, 4), ChoiceActionCard, AfterCardGainedListenerForSelf {

    init {
        addCoins = 5
        special = "You may trash an Action card from your hand. If you don’t, trash this. When you gain this, gain two Ruins."
        isTrashingCard = true
        textSize = 95
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.none { it.isAction }) {
            player.trashCardInPlay(this, true)
        } else {
            player.yesNoChoice(this, "Trash action card from hand? If you don't, ${this.cardNameWithBackgroundColor} will be trashed")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardFromHand(false, { c -> c.isAction })
        } else {
            player.trashCardInPlay(this, true)
        }
    }

    override fun afterCardGained(player: Player) {
        val ruins = listOfNotNull(player.gainRuins(), player.gainRuins())
        if (ruins.isNotEmpty()) {
            player.showInfoMessage("You gained ${ruins.groupedString} from ${this.cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Death Cart"
    }
}

