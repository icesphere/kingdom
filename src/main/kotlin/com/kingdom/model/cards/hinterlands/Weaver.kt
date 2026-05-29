package com.kingdom.model.cards.hinterlands

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardDiscardedListenerForSelf
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Weaver : HinterlandsCard(NAME, CardType.ActionReaction, 4), AfterCardDiscardedListenerForSelf, ChoiceActionCard {

    private enum class ChoiceType {
        PLAY,
        REACTION
    }

    init {
        special = "Gain two Silvers or a card costing up to \$4. When you discard this other than in Clean-up, you may play it."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoiceWithInfo(this,
                "Choose one for ${cardNameWithBackgroundColor}",
                ChoiceType.PLAY,
                Choice(1, "Gain two Silvers"),
                Choice(2, "Gain a card costing up to \$4"))
    }

    override fun afterCardDiscarded(player: Player) {
        player.yesNoChoice(this, "Play ${cardNameWithBackgroundColor} after discarding it?", ChoiceType.REACTION)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        when (info) {
            ChoiceType.PLAY -> {
                if (choice == 1) {
                    repeat(2) {
                        player.gainSupplyCard(Silver(), true)
                    }
                } else {
                    player.chooseSupplyCardToGainWithMaxCost(4)
                }
            }
            ChoiceType.REACTION -> if (choice == 1) {
                player.removeCard(this)
                if (player.isYourTurn) {
                    player.addActions(1, false)
                }
                player.playCard(this)
            }
        }
    }

    companion object {
        const val NAME: String = "Weaver"
    }
}
