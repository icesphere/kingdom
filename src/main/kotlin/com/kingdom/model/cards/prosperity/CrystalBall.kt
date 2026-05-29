package com.kingdom.model.cards.prosperity

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class CrystalBall : ProsperityCard(NAME, CardType.Treasure, 5), ChoiceActionCard {

    init {
        addCoins = 1
        special = "Look at the top card of your deck. You may trash it, discard it, play it if it is a Treasure or Action, or gain a copy of it."
        isTreasureExcludedFromAutoPlay = true
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val topCard = player.cardOnTopOfDeck ?: return
        val choices = mutableListOf(
                Choice(1, "Trash"),
                Choice(2, "Discard")
        )

        if (topCard.isTreasure || topCard.isAction) {
            choices.add(Choice(3, "Play"))
        }

        if (player.game.isCardAvailableInSupply(topCard) && !player.game.isCardNotInSupply(topCard)) {
            choices.add(Choice(4, "Gain copy"))
        }

        choices.add(Choice(5, "Leave on deck"))

        player.makeChoiceFromListWithInfo(this, "Top card: ${topCard.cardNameWithBackgroundColor}", topCard, choices)
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val topCard = info as Card

        when (choice) {
            1 -> player.removeTopCardOfDeck()?.let { player.cardTrashed(it, showLog = true) }
            2 -> player.removeTopCardOfDeck()?.let { player.addCardToDiscard(it, showLog = true) }
            3 -> {
                val cardToPlay = player.removeTopCardOfDeck()
                if (cardToPlay != null) {
                    if (cardToPlay.isAction) {
                        player.addActions(1)
                    }
                    player.playCard(cardToPlay)
                }
            }
            4 -> player.gainSupplyCard(topCard, true)
        }
    }

    companion object {
        const val NAME: String = "Crystal Ball"
    }
}
