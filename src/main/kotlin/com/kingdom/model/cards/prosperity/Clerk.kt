package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.TurnStartedListenerForCardsInHand
import com.kingdom.model.players.Player

class Clerk : ProsperityCard(NAME, CardType.ActionAttackReaction, 4), AttackCard, ChoiceActionCard, TurnStartedListenerForCardsInHand {

    init {
        addCoins = 2
        special = "Each other player with 5 or more cards in hand puts one onto their deck. At the start of your turn, you may play this from your hand."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents
                .filter { it.hand.size >= 5 }
                .forEach { it.addCardFromHandToTopOfDeck() }
    }

    override fun turnStarted(player: Player) {
        if (player.hand.contains(this)) {
            player.yesNoChoice(this, "Play ${this.cardNameWithBackgroundColor} from your hand?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && player.hand.contains(this)) {
            player.addActions(1)
            player.playCard(this)
        }
    }

    companion object {
        const val NAME: String = "Clerk"
    }
}
