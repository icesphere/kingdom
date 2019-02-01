package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class FortuneTeller : CornucopiaCard(NAME, CardType.ActionAttack, 3), AttackCard {

    init {
        addCoins = 2
        special = "Each other player reveals cards from the top of their deck until they reveal a Victory card or Curse. They put it on top and discard the rest."
        textSize = 100
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            val card = opponent.revealFromDeckUntilCardFoundAndDiscardOthers { c -> c.isVictory || c.isCurse }

            if (card != null) {
                opponent.addCardToTopOfDeck(card)
                opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor added ${card.cardNameWithBackgroundColor} to the top of your deck")
            }
        }
    }

    companion object {
        const val NAME: String = "Fortune Teller"
    }
}

