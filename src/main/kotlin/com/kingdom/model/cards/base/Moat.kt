package com.kingdom.model.cards.base

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.HandBeforeAttackListener
import com.kingdom.model.players.Player

class Moat : BaseCard(NAME, CardType.ActionReaction, 2), HandBeforeAttackListener, ChoiceActionCard {

    lateinit var attackCard: Card

    init {
        addCards = 2
        special = "When another player plays an Attack card, you may first reveal this from your hand, to be unaffected by it."
        isDefense = true
        fontSize = 13
        textSize = 81
    }

    override fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player) {
        this.attackCard = attackCard
        player.yesNoChoice(this, "Reveal $cardNameWithBackgroundColor to be unaffected by ${attackCard.cardNameWithBackgroundColor}?", attacker)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val attacker = info as Player
            attacker.showInfoMessage("${player.username} revealed $cardNameWithBackgroundColor to be unaffected by the attack")
            player.addEventLogWithUsername("revealed $cardNameWithBackgroundColor to be unaffected by ${attackCard.cardNameWithBackgroundColor}")
            attackCard.playersExcludedFromCardEffects.add(player)
        }
    }

    companion object {
        const val NAME: String = "Moat"
    }
}

