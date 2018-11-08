package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.SetAsideUntilStartOfTurnCard
import com.kingdom.model.cards.listeners.HandBeforeAttackListener
import com.kingdom.model.players.Player

class HorseTraders : CornucopiaCard(NAME, CardType.ActionReaction, 4), HandBeforeAttackListener, ChoiceActionCard, SetAsideUntilStartOfTurnCard {

    init {
        addBuys = 1
        addCoins = 3
        special = "Discard 2 cards. When another player plays an Attack card, you may first set this aside from your hand. If you do, then at the start of your next turn, +1 Card and return this to your hand."
        fontSize = 10
        textSize = 81
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsFromHand(2, false)
    }

    override fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player) {
        player.yesNoChoice(this, "Set aside ${this.cardNameWithBackgroundColor}? If you do, then at the start of your next turn, +1 Card and return this to your hand.")
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.removeCardFromHand(this)
            player.cardsSetAsideUntilStartOfTurn.add(this)
        }
    }

    override fun onStartOfTurn(player: Player) {
        player.drawCard()
        player.addCardToHand(this)
        player.addUsernameGameLog("added ${this.cardNameWithBackgroundColor} back to their hand")
    }

    companion object {
        const val NAME: String = "Horse Traders"
    }
}

