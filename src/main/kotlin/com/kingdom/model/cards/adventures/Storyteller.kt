package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Storyteller : AdventuresCard(NAME, CardType.Action, 5), ChooseCardsActionCard {

    init {
        addActions = 1
        addCoins = 1
        special = "Play up to 3 Treasures from your hand. Then pay all of your \$ (including the \$1 from this) and draw a card per \$1 you paid."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val numTreasures = player.hand.count { it.isTreasure }
        if (numTreasures > 0) {
            player.chooseCardsFromHand(special, numTreasures, true, this, { c -> c.isTreasure })
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        if (cards.isNotEmpty()) {
            cards.forEach {
                it.isHighlighted = false
                it.isSelected = false
                player.playCard(it, false, false, false)
            }

            player.game.treasureCardsPlayedInActionPhase.addAll(cards)

            player.refreshPlayerHandArea()
            player.game.refreshCardsPlayed()

            player.addEventLogWithUsername(" played: ${cards.groupedString}")
        }

        val numCoins = player.availableCoins
        player.addCoins(-1 * numCoins)
        player.addEventLogWithUsername(" paid \$$numCoins")
        player.drawCards(numCoins)
    }

    companion object {
        const val NAME: String = "Storyteller"
    }
}

