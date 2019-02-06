package com.kingdom.model.cards.empires.castles

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.empires.EmpiresCard
import com.kingdom.model.players.Player

class SmallCastle : EmpiresCard(NAME, CardType.ActionVictoryCastle, 5), TrashCardsForBenefitActionCard {

    init {
        victoryPoints = 2
        special = "Trash this or a Castle from your hand. If you do, gain a Castle."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { it.isCastle }) {
            player.trashCardsFromHandForBenefit(this, 1, "Trash a Castle from your hand, then gain a Castle", { c -> c.isCastle })
        } else {
            player.trashCardInPlay(this, true)
            player.showInfoMessage("There were no Castles in your hand, so $cardNameWithBackgroundColor was trashed")
            gainCastle(player)
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        gainCastle(player)
    }

    private fun gainCastle(player: Player) {
        if (player.game.availableCards.any { it.isCastle }) {
            player.chooseSupplyCardToGain(null, { c -> c.isCastle }, "Gain a Castle")
        } else {
            player.showInfoMessage("There were no Castles in the supply")
        }
    }

    override val pileName: String
        get() = Castles.NAME

    companion object {
        const val NAME: String = "Small Castle"
    }
}

