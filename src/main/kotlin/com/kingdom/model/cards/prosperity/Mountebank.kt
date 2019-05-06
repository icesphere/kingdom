package com.kingdom.model.cards.prosperity

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Mountebank : ProsperityCard(NAME, CardType.ActionAttack, 5), AttackCard, ChoiceActionCard {

    init {
        addCoins = 2
        special = "Each other player may discard a Curse. If they don’t, they gain a Curse and a Copper."
        isCurseGiver = true
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            if (opponent.hand.any { it.isCurse }) {
                opponent.makeChoice(this, Choice(1, "Discard Curse"), Choice(2, "Gain Curse and Copper"))
            } else {
                val curse = Curse()
                val copper = Copper()

                if (player.game.isCardAvailableInSupply(curse)) {
                    opponent.gainSupplyCard(curse, showLog = true)
                    opponent.gainSupplyCard(copper, showLog = true)
                    player.showInfoMessage("${opponent.username} gained a ${curse.cardNameWithBackgroundColor} and a ${copper.cardNameWithBackgroundColor}")
                    opponent.showInfoMessage("You gained a ${curse.cardNameWithBackgroundColor} and a ${copper.cardNameWithBackgroundColor} from ${player.username}'s $cardNameWithBackgroundColor")
                } else {
                    opponent.gainSupplyCard(copper, showLog = true)
                    player.showInfoMessage("${opponent.username} gained a ${copper.cardNameWithBackgroundColor}")
                    opponent.showInfoMessage("You gained a ${copper.cardNameWithBackgroundColor} from ${player.username}'s $cardNameWithBackgroundColor")
                }
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val curse = Curse()
        val copper = Copper()

        if (choice == 1) {
            player.discardCardFromHand(player.hand.first { it.isCurse })
            player.game.currentPlayer.showInfoMessage("${player.username} discarded a ${curse.cardNameWithBackgroundColor}")
        } else {
            if (player.game.isCardAvailableInSupply(curse)) {
                player.gainSupplyCard(curse, showLog = true)
                player.gainSupplyCard(copper, showLog = true)
                player.game.currentPlayer.showInfoMessage("${player.username} gained a ${curse.cardNameWithBackgroundColor} and a ${copper.cardNameWithBackgroundColor}")
            } else {
                player.gainSupplyCard(copper, showLog = true)
                player.game.currentPlayer.showInfoMessage("${player.username} gained a ${copper.cardNameWithBackgroundColor}")
            }
        }
    }

    companion object {
        const val NAME: String = "Mountebank"
    }
}

