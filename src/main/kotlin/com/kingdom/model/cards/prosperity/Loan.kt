package com.kingdom.model.cards.prosperity

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Loan : ProsperityCard(NAME, CardType.Treasure, 3), ChoiceActionCard {

    var treasureCard: Card? = null

    init {
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        addCoins = 1
        special = "When you play this, reveal cards from your deck until you reveal a Treasure. Discard it or trash it. Discard the other cards."
        isTrashingCard = true
        textSize = 84
    }

    override fun cardPlayedSpecialAction(player: Player) {
        var treasureFound = false

        val revealedCards = mutableListOf<Card>()

        while(!treasureFound) {
            val card = player.removeTopCardOfDeck()
            if (card != null) {
                revealedCards.add(card)
                if (card.isTreasure) {
                    treasureFound = true
                    treasureCard = card
                    player.makeChoice(this, "Discard or Trash ${card.cardNameWithBackgroundColor}?", Choice(1, "Discard"), Choice(2, "Trash"))
                } else {
                    player.addCardToDiscard(card)
                }
            } else {
                break
            }
        }

        if (revealedCards.isNotEmpty()) {
            player.addUsernameGameLog("revealed: ${revealedCards.groupedString}")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        val card = treasureCard!!

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

