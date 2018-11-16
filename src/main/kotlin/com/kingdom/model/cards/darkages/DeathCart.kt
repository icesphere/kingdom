package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class DeathCart : DarkAgesCard(NAME, CardType.ActionLooter, 4), ChoiceActionCard {

    init {
        testing = true
        addCoins = 5
        special = "You may trash an Action card from your hand. If you don’t, trash this. When you gain this, gain two Ruins."
        textSize = 95
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isEmpty()) {
            player.trashCardInPlay(this, true)
        } else {
            player.yesNoChoice(this, "Trash card from hand? If you don't, ${this.cardNameWithBackgroundColor} will be trashed")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardFromHand(false)
        } else {
            player.trashCardInPlay(this, true)
        }
    }

    companion object {
        const val NAME: String = "Death Cart"
    }
}

