package com.kingdom.model.cards.seaside

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class PirateShip : SeasideCard(NAME, CardType.ActionAttack, 4), AttackCard, ChoiceActionCard {

    init {
        testing = true
        special = "Choose one: +\$1 per Coin token on your Pirate Ship mat; or each other player reveals the top 2 cards of their deck, trashes one of those Treasures that you choose, and discards the rest, and then if anyone trashed a Treasure you add a Coin token to your Pirate Ship mat."
        fontSize = 11
        textSize = 100
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, Choice(1, "+\$${player.pirateCoinTokens}"), Choice(2, "Attack"))
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            player.addCoins(player.pirateCoinTokens)
        } else {
            player.triggerAttack(this)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents.forEach { opponent ->
            val topCardsOfDeck = opponent.removeTopCardsOfDeck(2)
            //todo
        }
    }

    companion object {
        const val NAME: String = "Pirate Ship"
    }
}

