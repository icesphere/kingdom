package com.kingdom.model.cards.darkages

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Catacombs : DarkAgesCard(NAME, CardType.Action, 5), ChoiceActionCard, AfterCardTrashedListenerForSelf {

    init {
        special = "Look at the top 3 cards of your deck. Choose one: Put them into your hand; or discard them and +3 Cards. When you trash this, gain a cheaper card."
        textSize = 113
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(3)
        if (cards.isNotEmpty()) {
            player.makeChoiceWithInfo(this, "The top 3 cards of your deck are: ${cards.groupedString}", cards, Choice(1, "Put into hand"), Choice(2, "Discard them and +3 Cards"))
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val cards = info as List<Card>

        if (choice == 1) {
            player.addUsernameGameLog("chose to put the cards into their hand")
            player.addCardsToHand(cards)
        } else {
            player.addUsernameGameLog("chose to discard the cards and draw 3 cards")
            player.addCardsToDiscard(cards)
            player.drawCards(3)
        }
    }

    override fun afterCardTrashed(player: Player) {
        player.chooseSupplyCardToGain(player.getCardCostWithModifiers(this) - 1)
    }

    companion object {
        const val NAME: String = "Catacombs"
    }
}

