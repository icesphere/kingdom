package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardDiscardedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.players.Player

class Trail : HinterlandsCard(NAME, CardType.ActionReaction, 4), AfterCardGainedListenerForSelf,
        AfterCardTrashedListenerForSelf, AfterCardDiscardedListenerForSelf, ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        special = "When you gain, trash, or discard this, other than in Clean-up, you may play it."
        fontSize = 11
    }

    override fun afterCardGained(player: Player) {
        maybePlay(player, "Play ${cardNameWithBackgroundColor} after gaining it?")
    }

    override fun afterCardTrashed(player: Player) {
        maybePlay(player, "Play ${cardNameWithBackgroundColor} after trashing it?")
    }

    override fun afterCardDiscarded(player: Player) {
        maybePlay(player, "Play ${cardNameWithBackgroundColor} after discarding it?")
    }

    private fun maybePlay(player: Player, text: String) {
        player.yesNoChoice(this, text)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.removeCard(this)
            player.game.trashedCards.remove(this)
            if (player.isYourTurn) {
                player.addActions(1, false)
            }
            player.playCard(this)
        }
    }

    companion object {
        const val NAME: String = "Trail"
    }
}
