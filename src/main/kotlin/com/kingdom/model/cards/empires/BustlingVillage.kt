package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class BustlingVillage : EmpiresCard(NAME, CardType.Action, 5), ChoiceActionCard {

    init {
        addCards = 1
        addActions = 3
        special = "Look through your discard pile. You may reveal a Settlers from it and put it into your hand. (Bustling Village is the bottom half of the Settlers pile.)"
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.cardsInDiscard.isEmpty()) {
            player.showInfoMessage("Discard pile is empty")
        } else {
            if (player.cardsInDiscard.any { it is Settlers }) {
                player.yesNoChoice(this, "Cards in discard: ${player.cardsInDiscard.groupedString}. Reveal ${Settlers().cardNameWithBackgroundColor} and put in into your hand?")
            } else {
                player.showInfoMessage("There were no Settlers in your discard. Cards in discard: ${player.cardsInDiscard.groupedString}")
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val card = player.cardsInDiscard.first { it is Settlers }
            player.removeCardFromDiscard(card)
            player.addCardToHand(card)
            player.addEventLogWithUsername("revealed ${card.cardNameWithBackgroundColor} from their discard pile and put it into their hand")
        }
    }

    override val pileName: String
        get() = Settlers.NAME

    companion object {
        const val NAME: String = "Bustling Village"
    }
}

