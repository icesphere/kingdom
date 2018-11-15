package com.kingdom.model.cards.prosperity

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Loan : ProsperityCard(NAME, CardType.Treasure, 3), ChoiceActionCard {

    init {
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        addCoins = 1
        special = "When you play this, reveal cards from your deck until you reveal a Treasure. Discard it or trash it. Discard the other cards."
        isTrashingCard = true
        textSize = 84
    }

    override fun cardPlayedSpecialAction(player: Player) {

        val card = player.revealFromDeckUntilCardFoundAndDiscardOthers { c -> c.isTreasure }

        if (card != null) {
            player.makeChoiceWithInfo(this, "Discard or Trash ${card.cardNameWithBackgroundColor}?", card, Choice(1, "Discard"), Choice(2, "Trash"))
        } else {
            val message = "No treasures found"
            player.addUsernameGameLog(message)
            player.showInfoMessage(message)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = info as Card

        if (choice == 1) {
            player.addCardToDiscard(card, showLog = true)
        } else {
            player.cardTrashed(card, true)
        }
    }

    companion object {
        const val NAME: String = "Loan"
    }
}

