package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackResolver
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.BeforeOpponentCardPlayedListener
import com.kingdom.model.players.Player

class Moat : KingdomCard(NAME, CardType.ActionReaction, 2), BeforeOpponentCardPlayedListener, ChoiceActionCard {

    lateinit var attackCard: Card

    init {
        testing = true
        addCards = 2
        special = "When another player plays an Attack card, you may first reveal this from your hand, to be unaffected by it."
        fontSize = 13
        textSize = 81
    }

    override fun onBeforeOpponentCardPlayed(card: Card, player: Player, opponent: Player) {
        if (card.isAttack && this.location == CardLocation.Hand) {
            attackCard = card
            player.yesNoChoice(this, "Reveal $cardNameWithBackgroundColor to be unaffected by ${card.cardNameWithBackgroundColor}?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            player.addGameLog("${player.username} revealed $cardNameWithBackgroundColor to be unaffected by ${attackCard.cardNameWithBackgroundColor}")
            attackCard.playersExcludedFromCardEffects.add(player)
        }
    }

    companion object {
        const val NAME: String = "Moat"
    }
}

