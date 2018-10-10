package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackResolver
import com.kingdom.model.players.Player

class Militia : KingdomCard(NAME, CardType.ActionAttack, 4), AttackResolver {

    init {
        addCoins = 2
        special = "Each other player discards down to 3 cards in their hand."
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        var addWaitingAction = false

        affectedOpponents
                .forEach { p ->
                    if (p.hand.size > 3) {
                        addWaitingAction = true
                        p.discardCardsFromHand(p.hand.size - 3, false)
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

