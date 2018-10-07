package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Militia : KingdomCard(NAME, CardType.ActionAttack, 4) {
    init {
        addCoins = 2
        special = "Each other player discards down to 3 cards in their hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        var addWaitingAction = false

        player.opponents
                .filter { !playersExcludedFromCardEffects.contains(it) }
                .forEach { p ->
                    if (p.hand.size > 3) {
                        addWaitingAction = true
                        p.discardCardsFromHand(p.hand.size - 3)
                    }
                }

        if (addWaitingAction) {
            player.waitForOtherPlayersToResolveActions()
        }
    }

    companion object {
        const val NAME: String = "Militia"
    }
}

