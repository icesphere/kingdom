package com.kingdom.model.cards.empires

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Sacrifice : EmpiresCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {

    init {
        special = "Trash a card from your hand. If it’s an: Action card, +2 Cards, +2 Actions; Treasure card, +\$2; Victory card, +2 VP"
        isAddCoinsCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val card = trashedCards.first()

        if (card.isAction) {
            player.drawCards(2)
            player.addActions(2)
            player.addEventLogWithUsername("gained +2 Cards, +2 Actions")
        }

        if (card.isTreasure) {
            player.addCoins(2)
            player.addEventLogWithUsername("gained +\$2")
        }

        if (card.isVictory) {
            player.addVictoryCoins(2)
            player.addEventLogWithUsername("gained +2 VP")
        }
    }

    companion object {
        const val NAME: String = "Sacrifice"
    }
}

