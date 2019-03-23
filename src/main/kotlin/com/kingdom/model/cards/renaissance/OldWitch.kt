package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class OldWitch : RenaissanceCard(NAME, CardType.ActionAttack, 5), AttackCard, ChoiceActionCard {

    init {
        addCards = 3
        special = "Each other player gains a Curse and may trash a Curse from their hand."
        isCurseGiver = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            val curse = Curse()
            if (opponent.game.isCardAvailableInSupply(curse)) {
                opponent.gainSupplyCard(curse, true)
                opponent.showInfoMessage("You gained a ${curse.cardNameWithBackgroundColor} from ${player.username}'s $cardNameWithBackgroundColor")
            }
            if (opponent.hand.any { it.isCurse }) {
                opponent.yesNoChoice(this, "Trash a ${curse.cardNameWithBackgroundColor} from your hand?")
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardFromHand(player.hand.first { it.isCurse })
        }
    }

    companion object {
        const val NAME: String = "Old Witch"
    }
}

