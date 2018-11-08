package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Province
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Explorer : SeasideCard(NAME, CardType.Action, 5), ChoiceActionCard {

    init {
        special = "You may reveal a Province from your hand. If you do, gain a Gold to your hand. If you don’t, gain a Silver to your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isProvince }) {
            player.yesNoChoice(this, "Reveal ${Province().cardNameWithBackgroundColor} to gain ${Gold().cardNameWithBackgroundColor} to your hand?")
        } else {
            player.acquireFreeCardFromSupply(Silver(), showLog = true, destination = CardLocation.Hand)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addUsernameGameLog("revealed a ${Province().cardNameWithBackgroundColor} from their hand")
            player.acquireFreeCardFromSupply(Gold(), showLog = true, destination = CardLocation.Hand)
        } else {
            player.acquireFreeCardFromSupply(Silver(), showLog = true, destination = CardLocation.Hand)
        }
    }

    companion object {
        const val NAME: String = "Explorer"
    }
}

