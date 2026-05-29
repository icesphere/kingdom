package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForCardsInPlay
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Cauldron : HinterlandsCard(NAME, CardType.TreasureAttack, 5), AttackCard, AfterCardGainedListenerForCardsInPlay {

    private var actionsGainedWhileInPlay = 0

    init {
        addCoins = 2
        addBuys = 1
        special = "The third time you gain an Action card this turn, each other player gains a Curse."
        isCurseGiver = true
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.isAction) {
            actionsGainedWhileInPlay++
            if (actionsGainedWhileInPlay == 3) {
                player.triggerAttack(this)
            }
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            opponent.gainSupplyCard(Curse(), true)
        }
    }

    override fun removedFromPlay(player: Player) {
        actionsGainedWhileInPlay = 0
        super.removedFromPlay(player)
    }

    companion object {
        const val NAME: String = "Cauldron"
    }
}
