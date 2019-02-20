package com.kingdom.model.cards.adventures

import com.kingdom.model.Game
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Giant : AdventuresCard(NAME, CardType.ActionAttack, 5), GameSetupModifier, AttackCard {

    init {
        special = "Turn your Journey token over (it starts face up). Then if it’s face down, +\$1. If it’s face up, +\$5, and each other player reveals the top card of their deck, trashes it if it costs from \$3 to \$6, and otherwise discards it and gains a Curse."
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowJourneyToken = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {

        player.isJourneyTokenFaceUp = !player.isJourneyTokenFaceUp

        if (player.isJourneyTokenFaceUp) {
            player.showInfoMessage("Journey token was flipped to face up")
            player.addInfoLogWithUsername("'s Journey token was flipped to face up")
            player.addCoins(5)
            attack(player, affectedOpponents)
        } else {
            player.showInfoMessage("Journey token was flipped to face down")
            player.addInfoLogWithUsername("'s Journey token was flipped to face down")
            player.addCoins(1)
        }
    }

    private fun attack(player: Player, affectedOpponents: List<Player>) {

        for (opponent in affectedOpponents) {
            val cards = opponent.removeTopCardsOfDeck(1, true)

            if (cards.isNotEmpty()) {
                val card = cards.first()
                val cost = player.getCardCostWithModifiers(card)
                if (cost in 3..6) {
                    opponent.showInfoMessage("${player.username}'s ${this.cardNameWithBackgroundColor} trashed your ${card.cardNameWithBackgroundColor}")
                    opponent.cardTrashed(card, true)
                } else {
                    var message = "${player.username}'s ${this.cardNameWithBackgroundColor} discarded your ${card.cardNameWithBackgroundColor}"
                    val curse = Curse()
                    if (player.game.isCardAvailableInSupply(curse)) {
                        message += " and gave you a ${curse.cardNameWithBackgroundColor}"
                    }
                    opponent.showInfoMessage(message)
                    opponent.addCardToDiscard(card, showLog = true)
                    opponent.gainSupplyCard(curse, true)
                }
            }
        }
    }

    companion object {
        const val NAME: String = "Giant"
    }
}

