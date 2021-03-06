package com.kingdom.model.cards.cornucopia

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Jester : CornucopiaCard(NAME, CardType.ActionAttack, 5), AttackCard, ChoiceActionCard {

    init {
        addCoins = 2
        isCurseGiver = true
        special = "Each other player discards the top card of their deck. If it’s a Victory card they gain a Curse; otherwise they gain a copy of the discarded card or you do, your choice."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            val card = opponent.discardTopCardOfDeck()
            if (card != null) {
                opponent.showInfoMessage("${player.username}'s ${this.cardNameWithBackgroundColor} discarded ${card.cardNameWithBackgroundColor} from your deck")

                if (card.isVictory) {
                    val curse = Curse()
                    if (opponent.game.isCardAvailableInSupply(curse)) {
                        opponent.showInfoMessage("You gained a ${curse.cardNameWithBackgroundColor} from ${player.username}'s ${this.cardNameWithBackgroundColor}")
                        opponent.gainSupplyCard(curse, true)
                        player.showInfoMessage("${opponent.username} gained a ${curse.cardNameWithBackgroundColor}")
                    }
                } else {
                    if (player.game.isCardAvailableInSupply(card)) {
                        player.makeChoiceWithInfo(this, "Who do you want to gain a copy of ${card.cardNameWithBackgroundColor}?", JesterInfo(card, opponent), Choice(1, "You"), Choice(2, opponent.username))
                    }
                }
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val jesterInfo = info as JesterInfo
        val playerToGainCard = if (choice == 1) player else {
            info.opponent.showInfoMessage("You gained ${info.card.cardNameWithBackgroundColor} from ${player.username}'s ${this.cardNameWithBackgroundColor}")
            info.opponent
        }
        playerToGainCard.gainSupplyCard(jesterInfo.card, showLog = true)
    }

    companion object {
        const val NAME: String = "Jester"
    }
}

class JesterInfo(val card: Card, val opponent: Player)

