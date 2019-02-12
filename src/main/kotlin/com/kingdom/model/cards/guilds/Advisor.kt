package com.kingdom.model.cards.guilds

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Advisor : GuildsCard(NAME, CardType.Action, 4), ChoiceActionCard {

    init {
        addActions = 1
        special = "Reveal the top 3 cards of your deck. The player to your left chooses one of them. Discard that card and put the rest into your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(3, true)

        when {
            cards.size == 1 -> player.addCardToDiscard(cards.first(), showLog = true)
            cards.isNotEmpty() -> {
                val choices = cards.mapIndexed { index, card -> Choice(index, card.name) }
                player.playerToLeft.makeChoiceFromListWithInfo(this, "Choose a card for ${player.username} to discard. The rest will go into their hand", cards, choices)
            }
            else -> player.showInfoMessage("There were no cards on top of your deck")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        @Suppress("UNCHECKED_CAST")
        val cards = info as List<Card>

        val currentPlayer = player.game.currentPlayer

        val cardToDiscard = cards[choice]
        currentPlayer.addCardToDiscard(cardToDiscard, showLog = true)
        currentPlayer.showInfoMessage("${player.username} discarded ${cardToDiscard.cardNameWithBackgroundColor}")

        val cardsToPutInHand = cards - cardToDiscard
        currentPlayer.addCardsToHand(cardsToPutInHand)

        currentPlayer.showInfoMessage("${cardsToPutInHand.groupedString} were added to your hand")
        currentPlayer.addEventLogWithUsername("added ${cardsToPutInHand.groupedString} to hand")
    }

    companion object {
        const val NAME: String = "Advisor"
    }
}

