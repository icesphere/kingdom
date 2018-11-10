package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class IllGottenGains : HinterlandsCard(NAME, CardType.Treasure, 5), AfterCardGainedListenerForSelf, ChoiceActionCard {

    init {
        addCoins = 1
        special = "When you play this, you may gain a Copper to your hand. When you gain this, each other player gains a Curse."
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        fontSize = 9
        textSize = 74
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.yesNoChoice(this, "Gain a ${Copper().cardNameWithBackgroundColor} to your hand?")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.gainSupplyCard(Copper(), showLog = true, destination = CardLocation.Hand)
        }
    }

    override fun afterCardGained(player: Player) {
        for (opponent in player.opponentsInOrder) {
            opponent.gainSupplyCard(Curse(), true)
        }
    }

    companion object {
        const val NAME: String = "Ill-Gotten Gains"
    }
}

