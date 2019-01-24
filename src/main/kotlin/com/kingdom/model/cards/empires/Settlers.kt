package com.kingdom.model.cards.empires

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.MultiTypePile
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Settlers : EmpiresCard(NAME, CardType.Action, 2), MultiTypePile, ChoiceActionCard {

    init {
        addCards = 1
        addActions = 1
        special = "Look through your discard pile. You may reveal a Copper from it and put it into your hand. (Settlers is the top half of the Bustling Village pile.)"
        textSize = 79
    }

    override val otherCardsInPile: List<Card>
        get() = listOf(BustlingVillage())

    override fun createMultiTypePile(game: Game): List<Card> {
        return listOf(
                Settlers(),
                Settlers(),
                Settlers(),
                Settlers(),
                Settlers(),
                BustlingVillage(),
                BustlingVillage(),
                BustlingVillage(),
                BustlingVillage(),
                BustlingVillage()
        )
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.cardsInDiscard.isEmpty()) {
            player.showInfoMessage("Discard pile is empty")
        } else {
            if (player.cardsInDiscard.any { it.isCopper }) {
                player.yesNoChoice(this, "Cards in discard: ${player.cardsInDiscard.groupedString}. Reveal ${Copper().cardNameWithBackgroundColor} and put in into your hand?")
            } else {
                player.showInfoMessage("There were no Coppers in your discard. Cards in discard: ${player.cardsInDiscard.groupedString}")
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            val card = player.cardsInDiscard.first { it.isCopper }
            player.removeCardFromDiscard(card)
            player.addCardToHand(card)
            player.addEventLogWithUsername("revealed ${card.cardNameWithBackgroundColor} from their discard pile and put it into their hand")
        }
    }

    companion object {
        const val NAME: String = "Settlers"
    }
}

