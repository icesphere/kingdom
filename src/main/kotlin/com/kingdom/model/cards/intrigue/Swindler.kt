package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class Swindler : IntrigueCard(NAME, CardType.ActionAttack, 3), AttackCard {

    init {
        addCoins = 2
        special = "Each other player trashes the top card of their deck and gains a card with the same cost that you choose."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents
                .forEach { opponent ->
                    val card = opponent.removeTopCardOfDeck()
                    if (card != null) {
                        opponent.cardTrashed(card)
                        val log = "${this.cardNameWithBackgroundColor} trashed ${opponent.username}'s ${card.cardNameWithBackgroundColor}"
                        player.addGameLog(log)
                        player.chooseCardForOpponentToGain(player.getCardCostWithModifiers(card), "$log. Choose a card from the supply to put on top of ${opponent.username}'s deck.", CardLocation.Deck, opponent)
                    }
                }
    }

    companion object {
        const val NAME: String = "Swindler"
    }
}

