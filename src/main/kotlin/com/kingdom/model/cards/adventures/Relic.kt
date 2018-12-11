package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class Relic : AdventuresCard(NAME, CardType.TreasureAttack, 5), AttackCard {

    init {
        addCoins = 2
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        special = "When you play this, each other player puts their -1 Card token on their deck, which will cause those players to draw one less card the next time they draw cards."
        textSize = 83
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            opponent.isMinusCardTokenOnDeck = true
            opponent.refreshPlayerHandArea()
        }
    }

    companion object {
        const val NAME: String = "Relic"
    }
}

