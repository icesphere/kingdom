package com.kingdom.model.cards.empires

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.MultiTypePile
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Catapult : EmpiresCard(NAME, CardType.ActionAttack, 3), MultiTypePile, AttackCard, TrashCardsForBenefitActionCard {

    init {
        addCoins = 1
        special = "Trash a card from your hand. If it costs \$3 or more, each other player gains a Curse. If it’s a Treasure, each other player discards down to 3 cards in hand. (Catapult is the top half of the Emporium pile.)"
        isPlayTreasureCardsRequired = true
    }

    override val otherCardsInPile: List<Card>
        get() = listOf(Rocks())

    override fun createMultiTypePile(game: Game): List<Card> {
        return listOf(
                Catapult(),
                Catapult(),
                Catapult(),
                Catapult(),
                Catapult(),
                Rocks(),
                Rocks(),
                Rocks(),
                Rocks(),
                Rocks()
        )
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand. If it costs \$3 or more, each other player gains a Curse. If it’s a Treasure, each other player discards down to 3 cards in hand.")
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        val card = trashedCards.first()

        if (player.getCardCostWithModifiers(card) >= 3) {
            player.triggerAttack(this, true)
        }

        if (card.isTreasure) {
            player.triggerAttack(this, false)
        }
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        val curseAttack = info as Boolean

        for (opponent in affectedOpponents) {
            if (curseAttack) {
                val curse = Curse()
                if (opponent.game.isCardAvailableInSupply(curse)) {
                    opponent.gainSupplyCard(curse, true)
                    opponent.showInfoMessage("You gained a ${curse.cardNameWithBackgroundColor} from ${player.username}'s $cardNameWithBackgroundColor")
                }
            } else {
                if (opponent.hand.size > 3) {
                    opponent.discardCardsFromHand(opponent.hand.size - 3, false)
                }
            }
        }
    }

    companion object {
        const val NAME: String = "Catapult"
    }
}

