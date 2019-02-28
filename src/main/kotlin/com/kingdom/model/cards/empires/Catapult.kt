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
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
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
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        if (player.hand.isNotEmpty()) {
            player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand. If it costs \$3 or more, each other player gains a Curse. If it’s a Treasure, each other player discards down to 3 cards in hand.", info = affectedOpponents)
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val card = trashedCards.first()

        @Suppress("UNCHECKED_CAST")
        val affectedOpponents = info as List<Player>

        if (player.getCardCostWithModifiers(card) >= 3) {
            attack(player, affectedOpponents, true)
        }

        if (card.isTreasure) {
            attack(player, affectedOpponents, false)
        }
    }

    private fun attack(player: Player, affectedOpponents: List<Player>, curseAttack: Boolean) {

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

        if (player.isOpponentHasAction) {
            player.waitForOtherPlayersToResolveActions()
        }
    }

    companion object {
        const val NAME: String = "Catapult"
    }
}

