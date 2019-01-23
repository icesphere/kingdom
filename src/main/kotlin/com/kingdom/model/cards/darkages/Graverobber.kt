package com.kingdom.model.cards.darkages

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Graverobber : DarkAgesCard(NAME, CardType.Action, 5), ChoiceActionCard, TrashCardsForBenefitActionCard {

    init {
        special = "Choose one: Gain a card from the trash costing from \$3 to \$6, onto your deck; or trash an Action card from your hand and gain a card costing up to \$3 more than it."
        fontSize = 11
        textSize = 116
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val availableCardsToGainFromTrash = player.game.trashedCards.filter { player.getCardCostWithModifiers(it) in 3..6 }
        val cardsInTrashInfo = if (availableCardsToGainFromTrash.isEmpty()) "There are no cards in the trash from \$3 to \$6. " else "Cards in trash from \$3 to \$6: ${availableCardsToGainFromTrash.groupedString}. "
        val text = cardsInTrashInfo + special
        player.makeChoice(this, text, Choice(1, "Gain card from trash"), Choice(2, "Trash action card"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            if (player.game.trashedCards.any { player.getCardCostWithModifiers(it) in 3..6 }) {
                player.isNextCardToTopOfDeck = true
                player.gainCardFromTrash(false, { c -> player.getCardCostWithModifiers(c) in 3..6 })
            } else {
                val message = "There were no cards in the trash costing from \$3 to \$6"
                player.addEventLog(message)
                player.showInfoMessage(message)
            }
        } else {
            if (player.hand.any { it.isAction }) {
                player.trashCardsFromHandForBenefit(this, 1, "Trash an Action card from your hand and gain a card costing up to \$3 more than it.", { c -> c.isAction })
            } else {
                player.addEventLogWithUsername("had no actions cards in their hand to trash")
                player.showInfoMessage("There are no actions cards in your hand to trash")
            }
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        val card = trashedCards.first()

        player.chooseSupplyCardToGain(player.getCardCostWithModifiers(card) + 3)
    }

    companion object {
        const val NAME: String = "Graverobber"
    }
}

