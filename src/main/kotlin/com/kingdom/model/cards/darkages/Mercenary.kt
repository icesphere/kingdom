package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Mercenary : DarkAgesCard(NAME, CardType.ActionAttack, 0), ChoiceActionCard, TrashCardsForBenefitActionCard, AttackCard {

    init {
        special = "You may trash 2 cards from your hand. If you do, +2 Cards, + \$2, and each other player discards down to 3 cards in hand. (This is not in the Supply.)"
        textSize = 119
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.size >= 2) {
            player.yesNoChoice(this, "Trash 2 cards from your hand for +2 Cards, + \$2, and each other player discards down to 3 cards in hand?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.trashCardsFromHandForBenefit(this, 2)
        }
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        player.drawCards(2)
        player.addCoins(2)
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            if (opponent.hand.size > 3) {
                opponent.discardCardsFromHand(opponent.hand.size - 3, false)
            }
        }
    }

    companion object {
        const val NAME: String = "Mercenary"
    }
}

