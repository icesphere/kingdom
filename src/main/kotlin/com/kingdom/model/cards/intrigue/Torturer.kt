package com.kingdom.model.cards.intrigue

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Torturer : IntrigueCard(NAME, CardType.ActionAttack, 5), AttackCard, ChoiceActionCard {

    init {
        addCards = 3
        special = "Each other player either discards 2 cards or gains a Curse to their hand, their choice. (They may pick an option they can’t do.)"
        textSize = 94
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents
                .forEach { opponent ->
                    opponent.makeChoice(this, Choice(1, "Discard 2 cards"), Choice(2, "Gain Curse to hand"))
                }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addEventLogWithUsername("chose discard 2 cards")
            player.discardCardsFromHand(2, false)
        } else {
            player.addEventLogWithUsername("chose to gain a Curse to hand")

            player.gainSupplyCardToHand(Curse(), false)
        }
    }

    companion object {
        const val NAME: String = "Torturer"
    }
}

