package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.players.Player

class Baron : IntrigueCard(NAME, CardType.Action, 4), ChoiceActionCard {

    init {
        addBuys = 1
        special = "You may discard an Estate for +\$4. If you don’t gain an Estate."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.count { it.name == Estate.NAME } == 0) {
            player.acquireFreeCardFromSupply(Estate())
        } else {
            player.yesNoChoice(this, "Discard an Estate for +\$4?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.discardCardFromHand(player.hand.first { it.name == Estate.NAME })
            player.addCoins(4)
        } else {
            player.addUsernameGameLog("gained an ${Estate().cardNameWithBackgroundColor}")
            player.acquireFreeCardFromSupply(Estate())
        }
    }

    companion object {
        const val NAME: String = "Baron"
    }
}

