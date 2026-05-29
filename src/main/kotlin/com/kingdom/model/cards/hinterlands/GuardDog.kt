package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.HandBeforeAttackListener
import com.kingdom.model.players.Player

class GuardDog : HinterlandsCard(NAME, CardType.ActionReaction, 3), HandBeforeAttackListener, ChoiceActionCard {

    init {
        addCards = 2
        special = "+2 Cards if you have 5 or fewer cards in hand. When another player plays an Attack card, you may first play this from your hand."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.size <= 5) {
            player.drawCards(2)
        }
    }

    override fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player) {
        if (player.hand.contains(this)) {
            player.yesNoChoice(this, "Play ${cardNameWithBackgroundColor} from your hand?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && player.hand.contains(this)) {
            player.playCard(this)
        }
    }

    companion object {
        const val NAME: String = "Guard Dog"
    }
}
